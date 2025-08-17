package org.dolniak.jtop.logger;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ACTION_LOG")
public class ActionLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private int pid;

    @Column(nullable = false)
    private LocalDateTime logtime;

    private String command;

    private String owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionType action;

    @Enumerated(EnumType.STRING)
    private ActionComment comment;

    public ActionLogEntity() {}

    public ActionLogEntity(int pid, ActionComment comment, ActionType action, String owner, String command, LocalDateTime logtime) {
        this.pid = pid;
        this.comment = comment;
        this.action = action;
        this.owner = owner;
        this.command = command;
        this.logtime = logtime;
    }

    public ActionComment getComment() {
        return comment;
    }

    public ActionType getAction() {
        return action;
    }

    public String getOwner() {
        return owner;
    }

    public String getCommand() {
        return command;
    }

    public LocalDateTime getLogtime() {
        return logtime;
    }

    public int getPid() {
        return pid;
    }

    public Long getId() {
        return id;
    }

    public void setComment(ActionComment comment) {
        this.comment = comment;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setLogtime(LocalDateTime logtime) {
        this.logtime = logtime;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public static class Builder {
        private final int pid;
        private final ActionType action;
        private final LocalDateTime logtime;

        private ActionComment comment = null;
        private String owner = null;
        private String command = null;

        public Builder(int pid, ActionType actionType) {
            this.pid = pid;
            this.action = actionType;
            this.logtime = LocalDateTime.now();
        }

        public Builder comment(ActionComment val) {
            comment = val; return this;
        }

        public Builder owner(String val) {
            owner = val; return this;
        }

        public Builder command(String val) {
            command = val; return this;
        }

        public ActionLogEntity build() {
            return new ActionLogEntity(this);
        }
    }

    private ActionLogEntity(Builder builder) {
        pid = builder.pid;
        comment = builder.comment;
        action = builder.action;
        owner = builder.owner;
        command = builder.command;
        logtime = builder.logtime;
    }
}
