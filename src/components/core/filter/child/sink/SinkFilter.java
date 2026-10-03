/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University.
 */
package components.core.filter.child.sink;

import java.io.FileWriter;
import java.io.IOException;

import annotation.PipeType;
import annotation.PipesFilter;
import components.core.filter.impl.CommonFilterImpl;

@PipesFilter(name = "SinkFilter", type = PipeType.SINK, description = "출력 파일로 저장하는 필터")
public class SinkFilter extends CommonFilterImpl {

    private String sinkFile = "Output.txt";

    public SinkFilter() {}
    public SinkFilter(String outputFile) { this.sinkFile = outputFile; }

    @Override
    public boolean specificComputationForFilter() throws IOException {
        try (FileWriter fw = new FileWriter(this.sinkFile)) {
            int data;
            while ((data = in.read()) != -1) {
                fw.write((char) data);
            }
            fw.flush();
        }
        System.out.println("[Sink] ::Filtering is finished; Output file is created -> " + sinkFile);
        return true;
    }

    public void setOutputFile(String outputFile) {
        this.sinkFile = outputFile;
    }
}