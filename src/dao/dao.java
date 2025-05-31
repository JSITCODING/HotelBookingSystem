package dao;

import java.io.IOException;
import java.util.List;

public interface dao<T, K> {
    void save(T obj) throws Exception;
    T findByID(K id);
    List<T> findAll();
    void update(T obj) throws IOException;
    void deleteById(K obj) throws IOException;

}
