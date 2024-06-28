package org.sample.backendfia.service;

import org.sample.backendfia.dto.CarreraDTO;

import java.util.List;

public interface IServiceCarrera {
    List<CarreraDTO> findAll();
    CarreraDTO findById(Long id);
    CarreraDTO save(CarreraDTO carreraDTO);
    void deleteById(Long id);
}
