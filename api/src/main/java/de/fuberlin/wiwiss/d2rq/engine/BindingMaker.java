package de.fuberlin.wiwiss.d2rq.engine;

import de.fuberlin.wiwiss.d2rq.algebra.NodeRelation;
import de.fuberlin.wiwiss.d2rq.algebra.ProjectionSpec;
import de.fuberlin.wiwiss.d2rq.nodes.NodeMaker;
import de.fuberlin.wiwiss.d2rq.sql.ResultRow;
import org.apache.jena.graph.Node;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingHashMap;
import org.apache.jena.sparql.engine.binding.BindingMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Produces {@link Binding}s from {@link ResultRow}s.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class BindingMaker {

    public static BindingMaker createFor(NodeRelation relation) {
        Map<Var, NodeMaker> vars = new HashMap<>();
        for (Var variable : relation.variables()) {
            vars.put(variable, relation.nodeMaker(variable));
        }
        return new BindingMaker(vars, null);
    }

    private final Map<Var, NodeMaker> nodeMakers;
    private final ProjectionSpec condition;

    public BindingMaker(Map<Var, NodeMaker> nodeMakers, ProjectionSpec condition) {
        this.nodeMakers = nodeMakers;
        this.condition = condition;
    }

    public Binding makeBinding(ResultRow row) {
        if (condition != null) {
            String value = row.get(condition);
            if (value == null || "false".equals(value) || "0".equals(value) || "".equals(value)) {
                return null;
            }
        }
        BindingMap result = new BindingHashMap();
        for (Var variableName : nodeMakers.keySet()) {
            Node node = nodeMakers.get(variableName).makeNode(row);
            if (node == null) {
                return null;
            }
            result.add(Var.alloc(variableName), node);
        }
        return result;
    }

    public Set<Var> variableNames() {
        return nodeMakers.keySet();
    }

    public NodeMaker nodeMaker(Var var) {
        return nodeMakers.get(var);
    }

    public ProjectionSpec condition() {
        return condition;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("BindingMaker(\n");
        for (Var variable : nodeMakers.keySet()) {
            result.append("    ");
            result.append(variable);
            result.append(" => ");
            result.append(nodeMakers.get(variable));
            result.append("\n");
        }
        result.append(")");
        if (condition != null) {
            result.append(" WHERE ");
            result.append(condition);
        }
        return result.toString();
    }

    public BindingMaker makeConditional(ProjectionSpec condition) {
        return new BindingMaker(nodeMakers, condition);
    }
}
