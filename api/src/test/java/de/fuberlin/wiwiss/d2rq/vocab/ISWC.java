/* CVS $Id: ISWC.java,v 1.1 2006/09/07 21:33:19 cyganiak Exp $ */
package de.fuberlin.wiwiss.d2rq.vocab;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * Vocabulary definitions from http://annotation.semanticweb.org/iswc/iswc.daml
 *
 * @author Auto-generated by schemagen on 06 Sep 2006 20:19
 */
public class ISWC {

    /**
     * <p>The namespace of the vocabulary as a string</p>
     */
    public static final String NS = "http://annotation.semanticweb.org/iswc/iswc.daml#";

    /**
     * <p>The namespace of the vocabulary as a string</p>
     *
     * @return String
     */
    public static String getURI() {
        return NS;
    }

    public static final Property persons_involved = property("persons_involved");

    public static final Property conference = property("conference");

    public static final Property formal_language = property("formal_language");

    public static final Property application_domain = property("application_domain");

    public static final Property algorithm = property("algorithm");

    public static final Property tool = property("tool");

    public static final Property hasSubtopic = property("hasSubtopic");

    public static final Property has_affiliate = property("has_affiliate");

    public static final Property is_about = property("is_about");

    public static final Property funding_by = property("funding_by");

    public static final Property involved_in_project = property("involved_in_project");

    public static final Property application = property("application");

    public static final Property has_affiliation = property("has_affiliation");

    public static final Property topic = property("topic");

    public static final Property author = property("author");

    public static final Property organizations_involved = property("organizations_involved");

    public static final Property research_topics = property("research_topics");

    public static final Property method = property("method");

    public static final Property name = property("name");

    public static final Property phone = property("phone");

    public static final Property country = property("country");

    public static final Property location = property("location");

    public static final Property email = property("email");

    public static final Property eventTitle = property("eventTitle");

    public static final Property address = property("address");

    public static final Property fax = property("fax");

    public static final Property first_Name = property("first_Name");

    public static final Property title = property("title");

    public static final Property year = property("year");

    public static final Property middle_Initial = property("middle_Initial");

    public static final Property date = property("date");

    public static final Property project_title = property("project_title");

    public static final Property last_Name = property("last_Name");

    public static final Property photo = property("photo");

    public static final Property homepage = property("homepage");

    public static final Resource Researcher = resource("Researcher");

    public static final Resource Research_Funding_Institution = resource("Research_Funding_Institution");

    public static final Resource Formal_Language = resource("Formal_Language");

    public static final Resource Event = resource("Event");

    public static final Resource Algorithm = resource("Algorithm");

    public static final Resource Application = resource("Application");

    public static final Resource Faculty_Member = resource("Faculty_Member");

    public static final Resource Full_Professor = resource("Full_Professor");

    public static final Resource Organization = resource("Organization");

    public static final Resource Association = resource("Association");

    public static final Resource Tutorial = resource("Tutorial");

    public static final Resource Employee = resource("Employee");

    public static final Resource Proceedings = resource("Proceedings");

    public static final Resource University = resource("University");

    public static final Resource PhDStudent = resource("PhDStudent");

    public static final Resource Topic = resource("Topic");

    public static final Resource Application_Domain = resource("Application_Domain");

    public static final Resource Institute = resource("Institute");

    public static final Resource Enterprise = resource("Enterprise");

    public static final Resource Project = resource("Project");

    public static final Resource Associate_Professor = resource("Associate_Professor");

    public static final Resource Person = resource("Person");

    public static final Resource Book = resource("Book");

    public static final Resource Method = resource("Method");

    public static final Resource Lecturer = resource("Lecturer");

    public static final Resource Tool = resource("Tool");

    public static final Resource Student = resource("Student");

    public static final Resource Report = resource("Report");

    public static final Resource Conference = resource("Conference");

    public static final Resource InProceedings = resource("InProceedings");

    public static final Resource Department = resource("Department");

    public static final Resource Workshop = resource("Workshop");

    public static final Resource Publication = resource("Publication");

    public static final Resource Development_of_Knowledge_Management_Systems = resource("Development_of_Knowledge_Management_Systems");

    public static final Resource e_Business = resource("e-Business");

    public static final Resource Knowledge_Reasoning = resource("Knowledge_Reasoning");

    public static final Resource Knowledge_Discovery = resource("Knowledge_Discovery");

    public static final Resource Text_Mining = resource("Text_Mining");

    public static final Resource World_Wide_Web = resource("World_Wide_Web");

    public static final Resource C = resource("C");

    public static final Resource OXML = resource("OXML");

    public static final Resource Knowledge_Systems = resource("Knowledge_Systems");

    public static final Resource Web_Services = resource("Web_Services");

    public static final Resource Information_Systems = resource("Information_Systems");

    public static final Resource Agents = resource("Agents");

    public static final Resource Logic = resource("Logic");

    public static final Resource Information_Extraction = resource("Information_Extraction");

    public static final Resource Agent_Systems = resource("Agent_Systems");

    public static final Resource Knowledge_Management = resource("Knowledge_Management");

    public static final Resource DAML_OIL = resource("DAML_OIL");

    public static final Resource Artificial_Intelligence = resource("Artificial_Intelligence");

    public static final Resource Semantic_Web = resource("Semantic_Web");

    public static final Resource KAON = resource("KAON");

    public static final Resource Databases = resource("Databases");

    public static final Resource RDFS = resource("RDFS");

    public static final Resource ISWC_2002 = resource("ISWC_2002");

    public static final Resource Data_Mining = resource("Data_Mining");

    public static final Resource Knowledge_Portals = resource("Knowledge_Portals");

    public static final Resource TowardsSemanticWebMining = ResourceFactory
            .createResource("http://annotation.semanticweb.org/iswc/Towards_Semantic_Web_Mining.html#TowardsSemanticWebMining");

    public static final Resource Knowledge_Management_Methodology = resource("Knowledge_Management_Methodology");

    public static final Resource Knowledge_Representation_Languages = resource("Knowledge_Representation_Languages");

    public static final Resource Ontology_Learning = resource("Ontology_Learning");

    public static final Resource Ontology_based_Knowledge_Management_Systems = resource("Ontology-based_Knowledge_Management_Systems");

    public static final Resource XML = resource("XML");

    public static final Resource Machine_Learning = resource("Machine_Learning");

    public static final Resource Network_Infrastructure = resource("Network_Infrastructure");

    public static final Resource Modeling = resource("Modeling");

    public static final Resource SQL = resource("SQL");

    public static final Resource Business_Engineering = resource("Business_Engineering");

    public static final Resource Information_Retrieval = resource("Information_Retrieval");

    public static final Resource Office_Information_Systems = resource("Office_Information_Systems");

    public static final Resource RDF = resource("RDF");

    public static final Resource Semantic_Annotation = resource("Semantic_Annotation");

    public static final Resource Java = resource("Java");

    public static final Resource Knowledge_Representation_And_Reasoning = resource("Knowledge_Representation_And_Reasoning");

    public static final Resource Semantic_Web_Iinfrastructure = resource("Semantic_Web_Iinfrastructure");

    public static final Resource Query_Languages = resource("Query_Languages");

    public static final Resource Matching = resource("Matching");

    public static final Resource Human_Computer_Interaction = resource("Human_Computer_Interaction");

    public static final Resource Semantic_Web_Languages = resource("Semantic_Web_Languages");

    public static final Resource Ontology_Engineering = resource("Ontology_Engineering");

    public static final Resource University_of_Karlsruhe = resource("University_of_Karlsruhe");

    public static final Resource AIFB = resource("AIFB");

    protected static Property property(String localName) {
        return ResourceFactory.createProperty(NS + localName);
    }

    protected static Resource resource(String localName) {
        return ResourceFactory.createResource(NS + localName);
    }

}