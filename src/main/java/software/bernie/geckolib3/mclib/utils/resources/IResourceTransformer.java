package software.bernie.geckolib3.mclib.utils.resources;

public interface IResourceTransformer {
    String transformDomain(String domain, String path);

    String transformPath(String domain, String path);

    String transform(String location);
}
