/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.rule.accessor;

import static org.kie.internal.ruleunit.RuleUnitUtil.RULE_UNIT_DECLARATION;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.drools.core.base.AcceptsClassObjectType;
import org.drools.core.base.ClassObjectType;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.kie.internal.ruleunit.RuleUnitDescription;

/**
 * A class capable of resolving a declaration in the current build context
 */
public class DeclarationScopeResolver {
    private final LinkedList<RuleConditionElement>   buildLinkedList;
    private final Map<String, Class<?>>              globalMap;
    private final InternalKnowledgePackage           pkg;

    private RuleImpl rule;
    private Optional<RuleUnitDescription> ruleUnitDescr = Optional.empty();

    protected DeclarationScopeResolver() {
        this( new HashMap<>(), new LinkedList<>() );
    }

    public DeclarationScopeResolver(final Map<String, Class<?>> globalMap, final LinkedList<RuleConditionElement> buildLinkedList) {
        this( globalMap, buildLinkedList, null );
    }

    public DeclarationScopeResolver(final Map<String, Class<?>> globalMap,
                                    final InternalKnowledgePackage pkg) {
        this( globalMap, new LinkedList<>(), pkg );
    }

    private DeclarationScopeResolver(Map<String, Class<?>> globalMap,
                                     LinkedList<RuleConditionElement> buildLinkedList,
                                     InternalKnowledgePackage pkg) {
        this.globalMap = globalMap;
        this.buildLinkedList = buildLinkedList;
        this.pkg = pkg;
    }

    public void setRule(RuleImpl rule) {
        this.rule = rule;
        this.ruleUnitDescr = pkg.getRuleUnitDescriptionLoader().getDescription(rule );
    }

    public RuleConditionElement peekBuildStack() {
        return buildLinkedList.peek();
    }

    public RuleConditionElement popBuildStack() {
        return buildLinkedList.pop();
    }

    public void pushOnBuildStack(RuleConditionElement element) {
        buildLinkedList.push(element);
    }

    private Declaration getExtendedDeclaration(RuleImpl rule, String identifier) {
        Declaration declaration = rule.getLhs().resolveDeclaration( identifier );
        if ( declaration != null ) {
            return declaration;
        }
        return rule.getParent() == null ? null : getExtendedDeclaration( rule.getParent(), identifier );

    }

    private Map<String, Declaration> getAllExtendedDeclaration(RuleImpl rule, Map<String, Declaration> dec) {
        dec.putAll( rule.getLhs().getInnerDeclarations() );
        if ( null != rule.getParent() ) {
            return getAllExtendedDeclaration( rule.getParent(),
                                              dec );
        }
        return dec;
    }

    public Declaration getDeclaration(String identifier) {
        // it may be a local bound variable
        for ( int i = this.buildLinkedList.size() - 1; i >= 0; i-- ) {
            final Declaration declaration = this.buildLinkedList.get( i ).resolveDeclaration( identifier );
            if ( declaration != null ) {
                return declaration;
            }
        }
        // look at parent rules
        if ( rule != null && rule.getParent() != null ) {
            // recursive algorithm for each parent
            //     -> lhs.getInnerDeclarations()
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(), identifier );
            if ( null != parentDeclaration ) {
                return parentDeclaration;
            }
        }

        // it may be a global or a rule unit variable
        Class<?> cls = resolveVarType( identifier );
        if ( cls != null ) {
            ClassObjectType classObjectType = new ClassObjectType( cls );

            Declaration declaration;
            final Pattern dummy = new Pattern( 0,
                                               classObjectType );

            ReadAccessor globalExtractor = new GlobalExtractor( identifier, classObjectType );//FieldAccessorFactory.get().getGlobalReadAccessor( identifier, classObjectType );
            declaration = new Declaration( identifier, globalExtractor, dummy );
            if ( pkg != null ) {

                // make sure dummy and globalExtractor are wired up to correct ClassObjectType
                // and set as targets for rewiring
                pkg.wireObjectType( classObjectType, dummy );
                pkg.wireObjectType( classObjectType, (AcceptsClassObjectType) globalExtractor );
            }
            return declaration;
        }
        return null;
    }

    public Class<?> resolveVarType( String identifier ) {
        return ruleUnitDescr.flatMap( unit -> unit.getVarType( identifier ) ) // resolve identifier on rule unit ...
                            .orElseGet( () -> globalMap.get( identifier ) );  // ... or alternatively among globals
    }

    public String normalizeValueForUnit( String value ) {
        return ruleUnitDescr.map( unit -> {
            int dotPos = value.indexOf( '.' );
            String firstPart = dotPos > 0 ? value.substring( 0, dotPos ) : value;
            return unit.hasVar( firstPart ) ? RULE_UNIT_DECLARATION + "." + value : value;
        }).orElse( value );
    }

    public boolean hasDataSource( String name ) {
        return ruleUnitDescr.map( descr -> descr.hasDataSource( name )).orElse( false );
    }

    public boolean available(RuleImpl rule,
                             final String name) {
        for ( int i = this.buildLinkedList.size() - 1; i >= 0; i-- ) {
            final Declaration declaration = buildLinkedList.get( i ).resolveDeclaration( name );
            if ( declaration != null ) {
                return true;
            }
        }
        if ( this.globalMap.containsKey( (name) ) ) {
            return true;
        }

        // look at parent rules
        if ( rule != null && rule.getParent() != null ) {
            // recursive algorithm for each parent
            //     -> lhs.getInnerDeclarations()
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(),
                                                                    name );
            if ( null != parentDeclaration ) {
                return true;
            }
        }
        return false;
    }

    public boolean isDuplicated(RuleImpl rule,
                                final String name,
                                final String type) {
        if ( this.globalMap.containsKey( (name) ) ) {
            return true;
        }

        for ( int i = this.buildLinkedList.size() - 1; i >= 0; i-- ) {
            final RuleConditionElement rce = buildLinkedList.get( i );
            final Declaration declaration = rce.resolveDeclaration( name );
            if ( declaration != null ) {
                // if it is an OR and it is duplicated, we can stop looking for duplication now
                // as it is a separate logical branch
                boolean inOr = ((rce instanceof GroupElement) && ((GroupElement) rce).isOr());
                if ( ! inOr || type == null ) {
                    return ! inOr;
                }
                return ! declaration.getDeclarationClass().getName().equals( type );
            }
        }
        // look at parent rules
        if ( rule != null && rule.getParent() != null ) {
            // recursive algorithm for each parent
            //     -> lhs.getInnerDeclarations()
            Declaration parentDeclaration = getExtendedDeclaration( rule.getParent(),
                                                                    name );
            return null != parentDeclaration;
        }
        return false;
    }

    public Map<String, Declaration> getDeclarations(RuleImpl rule) {
        return getDeclarations(rule, RuleImpl.DEFAULT_CONSEQUENCE_NAME);
    }

    /**
     * Return all declarations scoped to the current
     * RuleConditionElement in the build stack
     */
    public Map<String, Declaration> getDeclarations(RuleImpl rule, String consequenceName) {
        Map<String, Declaration> declarations = new HashMap<>();
        for (RuleConditionElement aBuildLinkedList : this.buildLinkedList) {
            // if we are inside of an OR we don't want each previous stack entry added because we can't see those variables
            if (aBuildLinkedList instanceof GroupElement && ((GroupElement)aBuildLinkedList).getType() == GroupElement.Type.OR) {
                continue;
            }

            // this may be optimized in the future to only re-add elements at
            // scope breaks, like "NOT" and "EXISTS"
            Map<String,Declaration> innerDeclarations = aBuildLinkedList instanceof GroupElement ?
                    ((GroupElement)aBuildLinkedList).getInnerDeclarations(consequenceName) :
                    aBuildLinkedList.getInnerDeclarations();
            declarations.putAll(innerDeclarations);
        }
        if ( null != rule.getParent() ) {
            return getAllExtendedDeclaration( rule.getParent(), declarations );
        }
        return declarations;
    }

    public Map<String,Class<?>> getDeclarationClasses(RuleImpl rule) {
        return getDeclarationClasses( getDeclarations( rule ) );
    }

    public static Map<String,Class<?>> getDeclarationClasses( final Map<String, Declaration> declarations) {
        final Map<String, Class<?>> classes = new HashMap<>();
        for ( Map.Entry<String, Declaration> decl : declarations.entrySet() ) {
            Class<?> declarationClass = decl.getValue().getDeclarationClass();
            // the declaration class could be null when there's a compilation error
            // that has been already reported somewhere else
            if (declarationClass != null) {
                classes.put( decl.getKey(), declarationClass );
            }
        }
        return classes;
    }

    public Pattern findPatternById(int id) {
        if ( !this.buildLinkedList.isEmpty() ) {
            return findPatternInNestedElements( id, buildLinkedList.get( 0 ) );
        }
        return null;
    }

    private Pattern findPatternInNestedElements(final int id,
                                                final RuleConditionElement rce) {
        for ( RuleConditionElement element : rce.getNestedElements() ) {
            if ( element instanceof Pattern ) {
                Pattern p = (Pattern) element;
                if (p.getPatternId() == id ) {
                    return p;
                }
            } else if ( !element.isPatternScopeDelimiter() ) {
                Pattern p = findPatternInNestedElements( id,
                                                         element );
                if ( p != null ) {
                    return p;
                }
            }
        }
        return null;
    }
}
