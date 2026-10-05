package es.codelearnacademy.filelab.xml;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import es.codelearnacademy.filelab.model.Vehiculo;
import es.codelearnacademy.filelab.repository.AbstractFileRepository;
import es.codelearnacademy.filelab.repository.IVehiculoRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class VehiculoXmlRepository
        extends AbstractFileRepository<Vehiculo, String>
        implements IVehiculoRepository {

    private final Path path;
    private final XmlMapper mapper = new XmlMapper();

    public VehiculoXmlRepository(Path path) {
        this.path = path;
    }

    @Override
    protected String getId(Vehiculo vehiculo) {
        return vehiculo.matricula();
    }

    @Override
    protected List<Vehiculo> readAll() throws IOException {
        DocumentoVehiculos documento = mapper.readValue(
                path.toFile(), DocumentoVehiculos.class);
        return documento.getVehiculos();
    }

    @Override
    protected void writeAll(List<Vehiculo> vehiculos) throws IOException {
        DocumentoVehiculos documento = new DocumentoVehiculos(vehiculos);
        mapper.writeValue(path.toFile(), documento);
    }
}
