package com.semantics.sparql.Models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.rdf4j.common.exception.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Map;

@Getter
@Setter
@RequestScope
@Component

public class Event {

    @JsonProperty("name")
    private String name;
    @JsonProperty("@type")
    private String type;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("endDate")
    private String endDate;



    public void validateSparql() throws IOException {
        ShaclSail shaclSail = new ShaclSail(new MemoryStore());


        SailRepository sailRepository = new SailRepository(shaclSail);
        sailRepository.init();

        try (SailRepositoryConnection connection = sailRepository.getConnection()) {

            connection.begin();

            String shape = "@prefix sh: <http://www.w3.org/ns/shacl#>.\n" +
                    "@prefix schema: <http://schema.org/>.\n" +
                    "@prefix wasa: <http://vocab.sti2.at/wasa/> .\n" +
                    "@prefix event: <http://example.org/events/> .\n" +
                    "@prefix : <http://http://example.org/eventapi/> .\n" +
                    "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                    "\n" +
                    ":EventSearch a schema:SearchAction;\n" +
                    "    schema:name \"Event Search\";\n" +
                    "    schema:description \"Search different types of events based on name or date\";\n" +
                    "    schema:actionStatus schema:PotentialActionStatus;\n" +
                    "    schema:target [\n" +
                    "        a schema:EntryPoint;\n" +
                    "        schema:urlTemplate :query;\n" +
                    "        schema:encodingFormat \"application/ld+json\";\n" +
                    "        schema:contentType \"application/ld+json\";\n" +
                    "        schema:httpMethod \"POST\"\n" +
                    "    ];\n" +
                    "    wasa:actionShape [\n" +
                    "        a sh:NodeShape;\n" +
                    "        sh:property [\n" +
                    "            sh:path schema:object;\n" +
                    "            sh:group wasa:Input;\n" +
                    "            sh:class schema:Event;\n" +
                    "            sh:node [\n" +
                    "                sh:property [\n" +
                    "                    sh:path schema:name;\n" +
                    "                    sh:datatype xsd:string;\n" +
                    "                    sh:maxCount 1\n" +
                    "                ];\n" +
                    "                sh:property [\n" +
                    "                    sh:path schema:startDate;\n" +
                    "                    sh:datatype xsd:date;\n" +
                    "                    sh:maxCount 1\n" +
                    "                ];\n" +
                    "                sh:property [\n" +
                    "                    sh:path schema:endDate;\n" +
                    "                    sh:datatype xsd:date;\n" +
                    "                    sh:maxCount 1\n" +
                    "                ]\n" +
                    "\n" +
                    "            ];\n" +
                    "            sh:minCount 1;\n" +
                    "            sh:maxCount 1;\n" +
                    "        ];\n" +
                    "         sh:sparql [\n" +
                    "\t a sh:SPARQLConstraint ;\n" +
                    "\t sh:prefixes rdf:;\n" +
                    "\t sh:prefixes schema:;\n" +
                    "\t sh:prefixes xsd:;\n" +
                    "\n" +
                    "\t sh:message \"Violation has occured\" ;\n" +
                    "\t sh:select '''\n" +
                    "\t   SELECT $this\n" +
                    "\t   WHERE {\n" +
                    "\t    ?event  a  schema:Event;\n" +
                    "            schema:name\t?name;\n" +
                    "            schema:eventSchedule ?schedule.\n" +
                    "\t    ?schedule schema:startDate ?sdate.\n" +
                    "\t    ?schedule schema:endDate ?edate.\n" +
                    "\t    filter(datatype(?sdate) = xsd:date && datatype(?edate) = xsd:date)\n" +
                    "\t    }\n" +
                    "\t    ''' ;\n" +
                    "\t ] ;\n" +
                    "        sh:property [\n" +
                    "            sh:path schema:result;\n" +
                    "            sh:group wasa:Output;\n" +
                    "            sh:nodeKind sh:IRI;\n" +
                    "            sh:class schema:Event;\n" +
                    "            sh:node [\n" +
                    "                sh:property [\n" +
                    "                    sh:path schema:name;\n" +
                    "                    sh:datatype xsd:string;\n" +
                    "                    sh:minCount 1;\n" +
                    "                ];\n" +
                    "                sh:property [\n" +
                    "                    sh:path schema:description;\n" +
                    "                    sh:datatype xsd:string;\n" +
                    "                    sh:minCount 1;\n" +
                    "                ];\n" +
                    "                sh:property [\n" +
                    "                    sh:path schema:startDate;\n" +
                    "                    sh:datatype xsd:date;\n" +
                    "\n" +
                    "                ];\n" +
                    "                sh:property [\n" +
                    "                    sh:path schema:endDate;\n" +
                    "                    sh:datatype xsd:date;\n" +
                    "\n" +
                    "                ]\n" +
                    "\n" +
                    "            ]\n" +
                    "\n" +
                    "        ]\n" +
                    "    ] .";

            StringReader shaclRules = new StringReader(shape);

            connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
            connection.commit();

            connection.begin();

            StringReader invalidSampleData = new StringReader(
                    String.join("\n", "",
                            "@prefix ex: <http://example.com/ns#> .",
                            "@prefix foaf: <http://xmlns.com/foaf/0.1/>.",
                            "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",

                            "ex:peter a foaf:Person ;",
                            "  foaf:age 20, \"30\"^^xsd:int  ."

                    ));

            connection.add(invalidSampleData, "", RDFFormat.TURTLE);
            try {
                connection.commit();
            } catch (RepositoryException exception) {
                Throwable cause = exception.getCause();
                if (cause instanceof ValidationException) {
                    Model validationReportModel = ((ValidationException) cause).validationReportAsModel();

                    Rio.write(validationReportModel, System.out, RDFFormat.TURTLE);
                }
                throw exception;
            }
        }
    }
    }




