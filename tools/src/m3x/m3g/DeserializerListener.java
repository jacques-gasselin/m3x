package m3x.m3g;

/**
 * A listener interface to allow tools to listen to the deserialization process.
 * This may be handy for creating tree controls describing the binary layout of
 * an m3g file. It may also be used for polishing assets as they are loaded.
 * 
 * @author jgasseli
 */
public interface DeserializerListener
{
    /**
     * Informs the listener when a section is about to be read. The prefix data is
     * read first before objects are deserialized. There is a checksum appended to
     * the section data that can not be read until the section data has been fully
     * read. The checksum is delivered later later in the {@code scetionEnded}
     * method.
     * 
     * @param compressionScheme the compression scheme used by the section
     * @param totalSectionLength the total compressed section length
     * @param uncompressedLength the total uncompressed data length
     * @see #sectionEnded(int)
     */
    public void sectionStarted(int compressionScheme, int totalSectionLength, int uncompressedLength);

    /**
     * Informs the listener that a section has ended with the given checksum.
     * @param checksum
     * @see #sectionStarted(int, int, int)
     */
    public void sectionEnded(int checksum);

    /**
     * Informs the listener that an object has been deserialized. The binary file
     * index as well as the object itself is sent to the listener. The listener
     * is allowed to modify the object or even return a different object. If the
     * returned object is not compatible with the passed in object the
     * deserialization may fail.
     * 
     * @param obj the deserialized object
     * @param objectIndex the binary file index of the object
     * @return the object to be used in the rest of the deserialization for the
     * given objectIndex.
     */
    public m3x.m3g.Object3D objectDeserialized(m3x.m3g.Object3D obj, int objectIndex);

    /**
     * Informs the listener that an object has been references. The binary file
     * index as well as the object itself is sent to the listener. The listener
     * is allowed to modify the object or even return a different object. If the
     * returned object is not compatible with the passed in object the
     * deserialization may fail.
     * 
     * @param obj the referenced object
     * @param objectIndex the binary file reference index of the object
     * @return the object to be used in this particular instance for the
     * given objectIndex.
     */
    public m3x.m3g.Object3D referenceDeserialized(m3x.m3g.Object3D obj, int objectIndex);
}
