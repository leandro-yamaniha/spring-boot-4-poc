package com.poc.delivery.infrastructure.persistence.repository;

import java.util.UUID;

import com.poc.delivery.infrastructure.persistence.entity.PedidoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<PedidoEntity, UUID> {
}
