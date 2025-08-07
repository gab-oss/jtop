package org.dolniak.jtop;

public record Process(int pid, String command, String owner, String state, long residentSetSize, double cumulativeCpu) { }