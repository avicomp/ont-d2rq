package de.fuberlin.wiwiss.d2rq.vocab;

import de.fuberlin.wiwiss.d2rq.D2RQException;
import de.fuberlin.wiwiss.d2rq.pp.PrettyPrinter;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Lists all the classes and properties in a schemagen-generated
 * vocabulary class, such as the {@link D2RQ} class.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
@SuppressWarnings("WeakerAccess")
public class VocabularySummarizer {
    private final Class<?> vocabularyJavaClass;
    private final String namespace;
    private final Set<Property> properties;
    private final Set<Resource> classes;

    public VocabularySummarizer(Class<?> vocabularyJavaClass) {
        this.vocabularyJavaClass = vocabularyJavaClass;
        namespace = findNamespace();
        properties = findAllProperties();
        classes = findAllClasses();
    }

    public Set<Property> getAllProperties() {
        return properties;
    }

    private Set<Property> findAllProperties() {
        Set<Property> results = new HashSet<>();
        for (int i = 0; i < vocabularyJavaClass.getFields().length; i++) {
            Field field = vocabularyJavaClass.getFields()[i];
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!Property.class.isAssignableFrom(field.getType())) continue;
            try {
                results.add((Property) field.get(null));
            } catch (IllegalAccessException ex) {
                throw new D2RQException(ex);
            }
        }
        return results;
    }

    public Set<Resource> getAllClasses() {
        return classes;
    }

    private Set<Resource> findAllClasses() {
        Set<Resource> results = new HashSet<>();
        for (int i = 0; i < vocabularyJavaClass.getFields().length; i++) {
            Field field = vocabularyJavaClass.getFields()[i];
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!Resource.class.isAssignableFrom(field.getType())) continue;
            if (Property.class.isAssignableFrom(field.getType())) continue;
            try {
                results.add((Resource) field.get(null));
            } catch (IllegalAccessException ex) {
                throw new D2RQException(ex);
            }
        }
        return results;
    }

    public String getNamespace() {
        return namespace;
    }

    private String findNamespace() {
        try {
            Object o = vocabularyJavaClass.getField("NS").get(vocabularyJavaClass);
            if (o instanceof String) {
                return (String) o;
            }
            return null;
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            return null;
        }
    }

    public Collection<Resource> getUndefinedClasses(Model model) {
        Set<Resource> result = new HashSet<>();
        StmtIterator it = model.listStatements(null, RDF.type, (RDFNode) null);
        while (it.hasNext()) {
            Statement stmt = it.nextStatement();
            if (stmt.getObject().isURIResource()
                    && stmt.getResource().getURI().startsWith(namespace)
                    && !classes.contains(stmt.getResource())) {
                result.add(stmt.getResource());
            }
        }
        return result;
    }

    public Collection<Property> getUndefinedProperties(Model model) {
        Set<Property> result = new HashSet<>();
        StmtIterator it = model.listStatements();
        while (it.hasNext()) {
            Statement stmt = it.nextStatement();
            if (stmt.getPredicate().getURI().startsWith(namespace)
                    && !properties.contains(stmt.getPredicate())) {
                result.add(stmt.getPredicate());
            }
        }
        return result;
    }

    public void assertNoUndefinedTerms(Model model, int undefinedPropertyErrorCode, int undefinedClassErrorCode) {
        Collection<Property> unknownProperties = getUndefinedProperties(model);
        if (!unknownProperties.isEmpty()) {
            throw new D2RQException(
                    "Unknown property " + PrettyPrinter.toString(
                            unknownProperties.iterator().next()) + ", maybe a typo?",
                    undefinedPropertyErrorCode);
        }
        Collection<Resource> unknownClasses = getUndefinedClasses(model);
        if (!unknownClasses.isEmpty()) {
            throw new D2RQException(
                    "Unknown class " + PrettyPrinter.toString(
                            unknownClasses.iterator().next()) + ", maybe a typo?",
                    undefinedClassErrorCode);
        }
    }
}