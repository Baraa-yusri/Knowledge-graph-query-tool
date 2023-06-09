package com.semantics.sparql.SparqlClient.Events;

import com.ontotext.graphdb.repository.http.GraphDBHTTPRepository;
import com.ontotext.graphdb.repository.http.GraphDBHTTPRepositoryBuilder;
import com.semantics.sparql.Models.Event;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Component

public class SearchEvent {

    Event event;


    public  SearchEvent(Event event){
        this.event = event;
    }

public void query(){

    //Connect to a remote repository
GraphDBHTTPRepository repository = new GraphDBHTTPRepositoryBuilder().
        withServerUrl("http://localhost:7200").withRepositoryId("statements").build();

        //Seperate connection from repository.
        RepositoryConnection repositoryConnection = repository.getConnection();

    String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
            "PREFIX schema: <https://schema.org/>" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "SELECT *\n" +
            "WHERE \n" +
            "  { ?event  a  schema:Event;\n" +
            "            schema:name ?name;\n" +
            "            schema:eventSchedule ?schedule.\n" +
            "    ?schedule schema:startDate ?sdate.\n" +
            "    ?schedule schema:endDate ?edate.\n" +
            "    filter(datatype(?sdate)!=xsd:dateTime &&?sdate >\""+event.getStartDate() +"\"^^xsd:date &&\n" +
            "    ?edate <\""+event.getEndDate()+"\"^^xsd:date)\n" +
            "} limit 10";

    System.out.println(query);

        try {

            TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL,
                    query);
            ArrayList res = new ArrayList<>();
            //new SPARQLResultsJSONWriter();
            TupleQueryResult tupleQueryResult = tupleQuery.evaluate();

            while (tupleQueryResult.hasNext()) {
                BindingSet bindingSet = tupleQueryResult.next();
                for (Binding binding : bindingSet) {
                   System.out.println(binding.getName() + " " + binding.getValue());
                }
            }

        tupleQueryResult.close();
        }finally {
            repositoryConnection.close();
        }

    }

}

