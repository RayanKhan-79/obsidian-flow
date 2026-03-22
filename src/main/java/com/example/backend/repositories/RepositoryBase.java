package com.example.backend.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.interfaces.Repository;
import com.example.backend.util.Util;

public abstract class RepositoryBase<T> implements Repository<T, Long> {

    protected final String tableName;
    protected final Database dbService;
    protected final Class<T> model;

    public RepositoryBase(String tableName, Database dbService, Class<T> model) {
        this.tableName = tableName;
        this.dbService = dbService;
        this.model = model;
    }

    @Override
    public Optional<T> Create(Object... params) 
    {
        try 
        {
            dbService.executeUpdate(InsertQuery(), params);
            return Find(dbService.GetInsertedId());
            
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> Find(Long Id) {
        try 
        {
            
            ResultSet result = dbService.executeQuery(
                String.format("SELECT * FROM %s WHERE id = ?", tableName),
                Id
            );

            return Util.MapResultToModel(result, model);

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean Delete(Long Id) 
    {
        try 
        {
            dbService.executeUpdate(
                String.format("DELETE FROM %s WHERE id = ?", tableName),
                Id
            );
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Boolean Update(Long Id, Map<String, Object> updates) 
    {
        try 
        {
            dbService.executeUpdate(
                String.format("UPDATE %s SET %s WHERE id = ?",
                    tableName, 
                    dbService.GetUpdateSQL(updates)
                ),
                Id
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected abstract String InsertQuery();    
}
