package com.example.demo2bot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.Hibernate;

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
    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_node_id")
    protected Node parent;
    @OneToMany(mappedBy = "parent")
    protected List<Node> childList = new LinkedList<>();
    @Column(name = "'name'")
    protected String name;
    @Column(name = "'name'")
    protected String text;
    protected Node(){}
    public Node(Node parent, String name, String text) {
        this.parent = parent;
        this.name = name;
        this.text = text;
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
        return text;
    }

    protected void addChild(Node childNode)
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
}
