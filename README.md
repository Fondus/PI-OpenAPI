# PI-OpenAPI
The Published Interface Open API is a set of FondUS's Java implements the custom plugins with base on Delft-FEWS system open API.

The Delft-FEWS system open API enables clients to extend DELFT-FEWS with their own plugins.
It's possible to write custom plugins above a below.
- Import module.
- Export module.
- Transformations module.

## PI-OpenAPI Transformations Module

The FondUS Java implements of the custome transformations modules.
Adding a custom transformation involves several steps which will be explained in detail in the upcoming section:

1. writing the code for the custom transformation.
2. configuring the custom transformation.

### Example by using the following formula:
**output = input1 x correctionFactor1 + input2 x correctionFactor2**

The code needed for such a transformation would be quite basic and is given below:

```java
package example;

import nl.wldelft.fews.openapi.transformationmodule.Calculation;
import nl.wldelft.fews.openapi.transformationmodule.Input;
import nl.wldelft.fews.openapi.transformationmodule.Output;
import nl.wldelft.util.timeseries.Variable;

public class Example1 implements Calculation {
    @Input
    float option1;
    
    @Input
    float option2;
    
    @Input
    Variable input1 = null;
    
    @Input
    Variable input2 = null;
    
    @Output
    Variable output = null;
    
    @Override
    public void calculate() throws Exception {
        if (Float.isNaN(input1.value) || Float.isNaN(input2.value)) return;
        output.value = (input1.value * option1 + input2.value * option2) / 2;
    }
}
```

Now the code above will explained step by step to understand the details.

1. Implements the interface Calculation. The method calculate() will contain the actual code for the transformation.
2. The input and output value need to be annotation.
    - @Input: It's can be FEWS TimeSeries or const factor.
    - @Output: The output FEWS TimeSeries.
3. @Input is important to note that input timeseries can be defined type as a:
    - Variable
    - TimSeriesArray
    - Variable[]
    - TimeSeriesArray[]
4. @Output can be defined type as a:
    - Variable
    - TimSeriesArray
5. The major difference between the use of the class Variable and TimeSeriesArray is that Variable is used when the output is only dependend on input values at the **same timestep or not**.
6. The same logic applies to the cases when a Variable[] or TimeSeriesArray[] is used. The difference between a Variable and a Variable[] is **timeseries or timeseries set**.
    - For example by configuration several timeseries are defined in the timeseriesSet config a Variable[] is used.
    - For example by configuration a locationSet in the timeSeriesSet, when the timeseriesSet only defines a single timeseries than a Variable is used.
7. If use Variable, calculate() method must be executed **timestep number** times. 
8. If use TimeSeriesArray, calculate() method must be executed **one** times. 

## PI-OpenAPI Import Module

The FondUS Java implements of the custome import modules.
Not implements yet.

## PI-OpenAPI Export Module

The FondUS Java implements of the custome export modules.
Not implements yet.
