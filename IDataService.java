package data;

import java.util.List;

public interface IDataService<T> {
    void save(T item);
    void update(T item);
    void delete(int id);
    T getById(int id);
    List<T> getAll();
}