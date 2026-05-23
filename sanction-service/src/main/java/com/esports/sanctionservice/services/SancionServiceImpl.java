package com.esports.sanctionservice.services;

import com.esports.sanctionservice.exceptions.SancionException;
import com.esports.sanctionservice.models.Sancion;
import com.esports.sanctionservice.models.dtos.SancionDTO;
import com.esports.sanctionservice.repositories.SancionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class SancionServiceImpl implements SancionService {

    private static final Logger log = LoggerFactory.getLogger(SancionServiceImpl.class);

    @Autowired
    private SancionRepository sancionRepository;

    @Override
    @Transactional(readOnly = true)
    public 

}
