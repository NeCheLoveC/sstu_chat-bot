package com.example.demo2bot.entities;

import jakarta.persistence.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class Node
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_node_id")
    protected Node parent;
    @OneToMany(mappedBy = "parent",fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy
    protected List<Node> childList = new LinkedList<>();
    @Column(name = "'name'")
    protected String name;
    @Column(name = "'text'", length = 10000)
    protected String text;
    public Node(){}
    public Node(Node parent, String name, String text) {
        this.parent = parent;
        this.name = name;
        this.text = text;

        //parent.addChild(this);
    }

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        if(text == null || text.isEmpty())
            return name;
        return text;
    }

    public void addChild(Node childNode)
    {
        this.childList.add(childNode);
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Node node = (Node) o;
        return getId() != null && Objects.equals(getId(), node.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean isRootNode() {
        return this.parent == null;
    }

    public boolean isNodeIsChildrenOfRootNode()
    {
        if(isRootNode())
            return false;
        return  parent.isRootNode();
    }

    public List<Node> getChildList() {
        return childList;
    }
}
