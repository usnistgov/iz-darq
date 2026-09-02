package gov.nist.healthcare.iz.darq.detections;

import org.immregistries.mqe.validator.detection.Detection;
import org.immregistries.mqe.validator.detection.DetectionLifecycle;
import org.immregistries.mqe.validator.detection.DetectionStatus;

import java.lang.reflect.Field;

public final class DetectionLifeCycleUtil {

    public static DetectionStatus statusFor(Detection d) {
        try {
            Field f = Detection.class.getField(d.name());
            return f.getAnnotation(DetectionStatus.class);
        } catch (NoSuchFieldException e) {
            // Can't happen - d.name() always names a field of its own enum class.
            return null;
        }
    }

    public static DetectionLifecycle lifecycleFor(Detection d) {
        DetectionLifecycle lifecycle = null;
        DetectionStatus status = statusFor(d);
        if (status != null) {
            lifecycle = status.status();
        }
        return lifecycle;

    }

    public static String lifecycleStringFor(Detection d) {
        DetectionLifecycle lifecycle = lifecycleFor(d);
        if (lifecycle == null) {
            return null;
        }
        else  {
            return lifecycle.name();
        }
    }
}
