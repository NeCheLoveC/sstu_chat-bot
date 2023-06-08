package com.example.demo2bot.services;

import com.example.demo2bot.model.Node;

import java.util.List;
import java.util.Optional;

public interface NodeService
{
    public List<Node> getNodesByParentId(Long id);
    public Optional<Node> getNodeWithChildren(Long id);
    public Optional<Node> getRootNode();
    public Long getIdRootNode();
    public void createRootNode();
}
