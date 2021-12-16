package zelenaLipa.api.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public class FileStorageServiceImpl implements FileStorageService {

    @Override
    public void init() {

    }

    @Override
    public void save(MultipartFile file) {

    }

    @Override
    public Resource load(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

}
