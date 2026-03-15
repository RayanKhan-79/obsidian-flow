package com.example.backend.util;

import java.sql.ResultSet;
import java.util.Optional;

public class Util 
{
    public static <T> Optional<T> MapResultToModel(ResultSet result, Class<T> type) 
    {
        try 
        {
            var constructor = type.getConstructor(ResultSet.class);
            if (result.next())
                return Optional.of(constructor.newInstance(result));
        } catch (Exception e) {
            return Optional.empty();
        }
        
        return Optional.empty();
    }    
}
