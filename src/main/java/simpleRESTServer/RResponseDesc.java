package simpleRESTServer;

public record RResponseDesc(String request, boolean successful, String message, RDesc data)
{}
