package com.example.demo2bot.services;

import com.example.demo2bot.model.Node;
import com.example.demo2bot.repo.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class NodeServiceImp implements NodeService
{
    NodeRepository nodeRepository;

    @Autowired
    public NodeServiceImp(NodeRepository nodeRepository)
    {
        this.nodeRepository = nodeRepository;
    }

    public List<Node> getNodesByParentId(Long id)
    {
        return nodeRepository.getNodesByParent(id);
    }

    public Optional<Node> getNodeWithChildren(Long id)
    {
        return nodeRepository.getNodesWithChildren(id);
    }

    @Override
    public Optional<Node> getRootNode()
    {
        return nodeRepository.getRootNode();
    }

    @Override
    public Long getIdRootNode()
    {
        return nodeRepository.getIdRootNode();
    }

    public void createRootNode()
    {
        if(!nodeRepository.getRootNode().isPresent())
        {
            Node rootNode = new Node(null,"Главное меню", "Главное меню");
            nodeRepository.save(rootNode);
        }
    }
}
