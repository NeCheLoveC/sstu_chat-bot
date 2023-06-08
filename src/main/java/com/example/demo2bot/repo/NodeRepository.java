package com.example.demo2bot.repo;

import com.example.demo2bot.model.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
public interface NodeRepository extends JpaRepository<Node,Long>
{
    @Query("select n from Node n where n.parent.id = :parentNodeId")
    public List<Node> getNodesByParent(Long parentNodeId);
    @Query("select n from Node n where n.id = :nodeId")
    public Optional<Node> getNodesWithChildren(Long nodeId);
    @Query("select n from Node n where n.parent is null")
    public Optional<Node> getRootNode();
    @Query("select distinct n.id from Node n where n.parent is null")
    public Long getIdRootNode();
}
