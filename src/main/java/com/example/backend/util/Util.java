package com.example.backend.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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
    
    public static <T> List<T> MapResultToModelList(ResultSet result, Class<T> type)
    {
        ArrayList<T> mapped = new ArrayList<>();
        try 
        {
            var constructor = type.getConstructor(ResultSet.class);
            while (result.next())
                mapped.add(constructor.newInstance(result));
        } catch (Exception e) {
            
        }
        return mapped;
    }
}
