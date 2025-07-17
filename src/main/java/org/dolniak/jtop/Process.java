package org.dolniak.jtop;

public record Process(int pid, String command, String owner) { }