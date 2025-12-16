package software.bernie.geckolib3.geo.raw.pojo;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FormatVersion {
	VERSION_1_12_0, VERSION_1_12_2, VERSION_1_14_0;

	@JsonValue
	public String toValue() {
        return switch (this) {
            case VERSION_1_12_0 -> "1.12.0";
			case VERSION_1_12_2 -> "1.12.2";
            case VERSION_1_14_0 -> "1.14.0";
        };
    }

	@JsonCreator
	public static FormatVersion forValue(String value) throws IOException {
		return switch (value) {
            case "1.12.0" -> VERSION_1_12_0;
            case "1.12.2" -> VERSION_1_12_2;
            case "1.14.0" -> VERSION_1_14_0;
            default -> throw new IOException("Cannot deserialize FormatVersion");
        };
    }
}
