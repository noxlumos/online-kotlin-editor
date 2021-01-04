package com.example.kotlinIDE.model;

public class Output {

    private String outputLine;
    private String streamName;

    public Output(String outputLine, String streamName) {
        this.outputLine = outputLine;
        this.streamName = streamName;
    }

    public String getOutputLine() {
        return outputLine;
    }

    public void setOutputLine(String outputLine) {
        this.outputLine = outputLine;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
}
