package software.bernie.geckolib3.geo.raw.pojo;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FormatVersion {
	VERSION_1_12_0, VERSION_1_12_2, VERSION_1_14_0;

	@JsonValue
	public String toValue() {
        switch (this) {
            case VERSION_1_12_0: return "1.12.0";
			case VERSION_1_12_2: return "1.12.2";
            case VERSION_1_14_0: return "1.14.0";
            default: throw new IllegalStateException("Unexpected value: " + this);
        }
    }

	@JsonCreator
	public static FormatVersion forValue(String value) throws IOException {
        switch (value) {
            case "1.12.0": return VERSION_1_12_0;
            case "1.12.2": return VERSION_1_12_2;
            case "1.14.0": return VERSION_1_14_0;
            default: throw new IOException("Cannot deserialize FormatVersion");
        }
    }
}
