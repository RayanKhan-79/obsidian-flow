package com.example.backend.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.backend.database.Database;
import com.example.backend.models.User;
import com.example.backend.util.Util;
import com.example.backend.enums.Permissions;

public class UserRepo extends RepositoryBase<User> {

    private final String permissionsTableName = "user_permissions";

    public UserRepo(Database dbService) 
    {
        super("users", dbService, User.class);
    }

    @Override
    protected String InsertQuery() {
        return String.format(
            "INSERT INTO %s (first_name, last_name, email, password) VALUES (?, ?, ?, ?)",
            tableName
        );
    }
    
    public Optional<User> FindByEmailAndPassword(String email, String password)
    {
        ResultSet result =  dbService.executeQuery(
            String.format("SELECT * FROM %s WHERE email = ? AND password = ?", tableName),
            email, password
        );
        return Util.MapResultToModel(result, model);
    }

    public Optional<User> FindByIdentifierAndPassword(String identifier, String password)
    {
        ResultSet result = dbService.executeQuery(
            String.format(
                "SELECT * FROM %s WHERE (email = ? OR substr(email, 1, instr(email, '@') - 1) = ?) AND password = ?",
                tableName
            ),
            identifier,
            identifier,
            password
        );
        return Util.MapResultToModel(result, model);
    }

    public List<User> GetAll()
    {
        ResultSet result = dbService.executeQuery(
            String.format("SELECT * FROM %s ORDER BY id", tableName)
        );
        return Util.MapResultToModelList(result, model);
    }

    public boolean ExistsByEmail(String email)
    {
        try
        {
            ResultSet result = dbService.executeQuery(
                String.format("SELECT 1 FROM %s WHERE email = ? LIMIT 1", tableName),
                email
            );
            return result.next();
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean AddPermission(Long Id, Permissions permission)
    {
        try 
        {
            dbService.executeUpdate(
                String.format("INSERT INTO %s (user_id, permission) VALUES (?, ?)", permissionsTableName),
                Id, permission.name()
            );

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public Boolean RemovePermission(Long Id, Permissions permission)
    {
        try 
        {
            dbService.executeUpdate(String.format("""
                    DELETE FROM %s 
                    WHERE user_id = ?
                    AND permission = ?        
                """, permissionsTableName),
                Id, permission.name()
            );

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    
    public Boolean HasPermission(Long Id, Permissions permission)
    {
        try 
        {
            ResultSet result = dbService.executeQuery(String.format("""
                    SELECT 1 FROM %s
                    WHERE user_id = ?
                    AND permission = ?        
                """, permissionsTableName),
                Id, permission.name()
            );

            return result.next();
        } catch (SQLException e) {
            return false;
        }
    }



    public List<Permissions> GetPermissions(Long Id)
    {
        ResultSet result = dbService.executeQuery(
            String.format("SELECT * FROM %s WHERE user_id = ?", permissionsTableName),
            Id
        );

        return Util.MapResultToModelList(result, Permissions.class);
    }
}
