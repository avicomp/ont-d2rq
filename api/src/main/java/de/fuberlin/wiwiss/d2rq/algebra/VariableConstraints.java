package de.fuberlin.wiwiss.d2rq.algebra;

import de.fuberlin.wiwiss.d2rq.expr.Conjunction;
import de.fuberlin.wiwiss.d2rq.expr.Expression;
import de.fuberlin.wiwiss.d2rq.nodes.NodeMaker;
import de.fuberlin.wiwiss.d2rq.nodes.NodeSetConstraintBuilder;
import de.fuberlin.wiwiss.d2rq.nodes.NodeSetFilter;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;

import java.util.*;

/**
 * A map from variables to {@link NodeMaker}s that helps to build up
 * constraints in cases where a variable is bound multiple times.
 * <p>
 * If several node makers are added for the same variable name, then only
 * one will be kept, and a constraint expression will be built up. The
 * expression ensures that both identically-named node makers produce the
 * same node for any result row that matches the expression.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class VariableConstraints {
    private final Map<Var, NodeSetFilter> nodeSets = new HashMap<>();
    private final Map<Var, NodeMaker> nodeMakers = new HashMap<>();
    private final Map<Var, AliasMap> nodeRelationAliases = new HashMap<>();
    private final Set<ProjectionSpec> projections = new HashSet<>();

    public void add(Var var, NodeMaker nodeMaker, AliasMap aliases) {
        if (!nodeMakers.containsKey(var)) {
            nodeMakers.put(var, nodeMaker);
            projections.addAll(nodeMaker.projectionSpecs());
        }
        if (!nodeSets.containsKey(var)) {
            nodeSets.put(var, new NodeSetConstraintBuilder());
        }
        NodeSetFilter nodeSet = nodeSets.get(var);
        nodeMaker.describeSelf(nodeSet);
        if (!nodeRelationAliases.containsKey(var)) {
            nodeRelationAliases.put(var, aliases);
        }
    }

    public void addIfVariable(Node possibleVariable, NodeMaker nodeMaker, AliasMap aliases) {
        if (!possibleVariable.isVariable()) return;
        add((Var) possibleVariable, nodeMaker, aliases);
    }

    public void addAll(NodeRelation nodeRelation) {
        for (Var variable : nodeRelation.variables()) {
            add(variable, nodeRelation.nodeMaker(variable), nodeRelation.baseRelation().aliases());
        }
    }

    /**
     * @return <tt>false</tt> if two identically-named node makers cannot produce the same node
     */
    public boolean satisfiable() {
        return !constraint().isFalse();
    }

    /**
     * @return An expression that, if it holds for a result row, will ensure that
     * any two identically-named node makers produce the same node
     */
    public Expression constraint() {
        Collection<Expression> expressions = new ArrayList<>();
        for (Var var : nodeSets.keySet()) {
            NodeSetConstraintBuilder nodeSet = (NodeSetConstraintBuilder) nodeSets.get(var);
            if (nodeSet.isEmpty()) {
                return Expression.FALSE;
            }
            expressions.add(nodeSet.constraint());
        }
        return Conjunction.create(expressions);
    }

    /**
     * @return A regular map from variable names to {@link NodeMaker}s
     */
    public Map<Var, NodeMaker> toMap() {
        return nodeMakers;
    }

    public Set<Var> allNames() {
        return nodeMakers.keySet();
    }

    public Map<Var, AliasMap> relationAliases() {
        return nodeRelationAliases;
    }

    /**
     * @return All projections needed by any of the retained node makers
     */
    public Set<ProjectionSpec> allProjections() {
        return projections;
    }
}
