package com.example.backend.interfaces;

import java.util.Map;
import java.util.Optional;

public interface Repository <T, PK>
{
    Optional<T> Create(Object... params);
    Optional<T> Find(PK Id);
    Boolean Delete(PK Id);
    Boolean Update(PK Id, Map<String, Object> updates);
}
