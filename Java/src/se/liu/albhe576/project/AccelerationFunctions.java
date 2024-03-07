package se.liu.albhe576.project;



public class AccelerationFunctions {
    public AccelerationFunctions(){
        this.paths = new AccelerationFunction[][]{
                {AccelerationFunctions.sinAcceleration,     AccelerationFunctions.cosAccelerationMS},
                {AccelerationFunctions.sinAcceleration,     AccelerationFunctions.ms},
                {AccelerationFunctions.noMovement,          AccelerationFunctions.sinAccelerationMS},
                {AccelerationFunctions.cosAcceleration,     AccelerationFunctions.bossMovementY},
                {AccelerationFunctions.cosAccelerationMS,   AccelerationFunctions.sinAcceleration},
                {AccelerationFunctions.ms,                  AccelerationFunctions.sinAcceleration},
                {AccelerationFunctions.sinAccelerationMS,   AccelerationFunctions.noMovement},
                {AccelerationFunctions.noMovement,          AccelerationFunctions.ms},
                {AccelerationFunctions.halvedCosAccelerationMS,   AccelerationFunctions.halvedSinAccelerationMS},
                {AccelerationFunctions.negativeHalvedCosAccelerationMS,   AccelerationFunctions.halvedSinAccelerationMS},
        };
    }
    private final AccelerationFunction[][] paths;
    public AccelerationFunction[] getPath(int index){
        return this.paths[index];
    }
    private static final AccelerationFunction sinAcceleration      = (t, e) -> (float)Math.sin(t / 500.0f) / 5.0f;
    private static final AccelerationFunction cosAcceleration      = (t, e) -> (float)Math.cos(t / 500.0f) / 5.0f;
    private static final AccelerationFunction ms                   = (t, e) -> e.getMovementSpeed();
    private static final AccelerationFunction sinAccelerationMS    = (t, e) -> ms.apply(t,e) + sinAcceleration.apply(t,e);
    private static final AccelerationFunction cosAccelerationMS    = (t, e) -> ms.apply(t,e) + cosAcceleration.apply(t,e);
    private static final AccelerationFunction bossMovementY        = (t,e) -> e.y >= 50.0f ? 0.2f : sinAcceleration.apply(t,e);
    private static final AccelerationFunction noMovement           = (t,e) -> 0.0f;
    private static final AccelerationFunction halvedSinAccelerationMS = (t,e) -> 0.5f * ms.apply(t,e) +  sinAcceleration.apply(t,e);
    private static final AccelerationFunction halvedCosAccelerationMS = (t,e) -> 0.5f * ms.apply(t,e) +  cosAcceleration.apply(t,e);

    private static final AccelerationFunction negativeHalvedCosAccelerationMS = (t,e) -> -0.5f * ms.apply(t,e) + cosAcceleration.apply(t,e);

}
