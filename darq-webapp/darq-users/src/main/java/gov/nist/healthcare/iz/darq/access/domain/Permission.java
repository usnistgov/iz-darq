package gov.nist.healthcare.iz.darq.access.domain;


public enum Permission {
    SUPER(),
    CONFIG_PUBLIC_VIEW(
        Scope(
                Scope.GLOBAL,
                ActionOn(
                        ResourceType.CONFIGURATION,
                        PUBLIC(Action.VIEW),
                        OWNED(Action.VIEW)
                )
        )
    ),
    CONFIG_PRIVATE_AUTHOR(
            CONFIG_PUBLIC_VIEW,
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.CONFIGURATION,
                            ANY(
                                    Action.CREATE
                            ),
                            OWNED(
                                    Action.EDIT,
                                    Action.CLONE,
                                    Action.DELETE
                            ),
                            PUBLIC(
                                    Action.CLONE
                            )
                    )
            )
    ),
    CONFIG_PUBLIC_AUTHOR(
            CONFIG_PRIVATE_AUTHOR,
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.CONFIGURATION,
                            PUBLIC(
                                    Action.VIEW,
                                    Action.EDIT,
                                    Action.DELETE
                            )
                    )
            )
    ),
    CONFIG_PUBLISH(
            CONFIG_PUBLIC_AUTHOR,
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.CONFIGURATION,
                            OWNED(
                                    Action.PUBLISH
                            )
                    )
            )
    ),
    RT_PUBLIC_VIEW(
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.REPORT_TEMPLATE,
                            PUBLIC(Action.VIEW),
                            OWNED(Action.VIEW)
                    )
            )
    ),
    RT_PRIVATE_AUTHOR(
            RT_PUBLIC_VIEW,
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.REPORT_TEMPLATE,
                            ANY(
                                    Action.CREATE
                            ),
                            OWNED(
                                    Action.EDIT,
                                    Action.CLONE,
                                    Action.DELETE
                            ),
                            PUBLIC(
                                    Action.CLONE
                            )
                    )
            )
    ),
    RT_PUBLIC_AUTHOR(
            RT_PRIVATE_AUTHOR,
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.REPORT_TEMPLATE,
                            PUBLIC(
                                    Action.VIEW,
                                    Action.EDIT,
                                    Action.DELETE
                            )
                    )
            )
    ),
    RT_PUBLISH(
            RT_PUBLIC_AUTHOR,
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.REPORT_TEMPLATE,
                            OWNED(
                                    Action.PUBLISH
                            )
                    )
            )
    ),
    DATA_PRIVATE_AUTHOR(
            Scope(
                    Scope.GLOBAL,
                    ActionOn(
                            ResourceType.ADF,
                            ANY(
                                    Action.UPLOAD
                            ),
                            OWNED(
                                    Action.VIEW,
                                    Action.ANALYSE,
                                    Action.DELETE
                            )
                    ),
                    ActionOn(
                            ResourceType.ANALYSIS_JOB,
                            OWNED(
                                    Action.VIEW,
                                    Action.DELETE
                            )
                    ),
                    ActionOn(
                            ResourceType.REPORT,
                            OWNED(
                                    Action.VIEW,
                                    Action.COMMENT,
                                    Action.DELETE
                            )
                    )
            )
    ),
    DATA_FACILITY_VIEW(
            Scope(
                    Scope.FACILITY,
                    ActionOn(
                            ResourceType.ADF,
                            ANY(
                                    Action.VIEW
                            )
                    ),
                    ActionOn(
                            ResourceType.REPORT,
                            PUBLIC(
                                    Action.VIEW
                            )
                    )
            )
    ),
    DATA_FACILITY_UPLOAD(
            DATA_FACILITY_VIEW,
            Scope(
                    Scope.FACILITY,
                    ActionOn(
                            ResourceType.ADF,
                            ANY(
                                    Action.UPLOAD
                            ),
                            OWNED(
                                    Action.DELETE
                            )
                    )
            )
    ),
    DATA_FACILITY_AUTHOR(
            DATA_FACILITY_UPLOAD,
            Scope(
                    Scope.FACILITY,
                    ActionOn(
                            ResourceType.ADF,
                            ANY(
                                    Action.ANALYSE,
                                    Action.DELETE
                            )
                    ),
                    ActionOn(
                            ResourceType.ANALYSIS_JOB,
                            OWNED(
                                    Action.VIEW,
                                    Action.DELETE
                            )
                    ),
                    ActionOn(
                            ResourceType.REPORT,
                            OWNED(
                                    Action.VIEW,
                                    Action.COMMENT,
                                    Action.DELETE
                            )
                    )
            )
    ),
    DATA_FACILITY_PUBLISH(
            DATA_FACILITY_AUTHOR,
            Scope(
                    Scope.FACILITY,
                    ActionOn(
                            ResourceType.REPORT,
                            ANY(
                                    Action.PUBLISH
                            ),
                            PUBLIC(
                                    Action.DELETE
                            )
                    )
            )
    );


    public final ScopeResourceTokenAction[] scopes;
    public final Permission implicit;

    Permission(Permission implicit, ScopeResourceTokenAction... scopes) {
        this.scopes = scopes;
        this.implicit = implicit;
    }

    Permission(ScopeResourceTokenAction... scopes) {
        this(null, scopes);
    }

    private static ScopeResourceTokenAction Scope(Scope scope, ResourceTokenAction... resourceTokenActions) {
        return new ScopeResourceTokenAction(scope, resourceTokenActions);
    }

    private static ResourceTokenAction ActionOn(ResourceType resourceType, TokenAction... tokenActions) {
        return new ResourceTokenAction(resourceType, tokenActions);
    }

    private static TokenAction OWNED(Action... actions) {
        return new TokenAction(AccessToken.OWNER, actions);
    }

    private static TokenAction PARTICIPANT(Action... actions) {
        return new TokenAction(AccessToken.PARTICIPANT, actions);
    }

    private static TokenAction PUBLIC(Action... actions) {
        return new TokenAction(AccessToken.PUBLIC, actions);
    }

    private static TokenAction ANY(Action... actions) {
        return new TokenAction(AccessToken.ANY, actions);
    }
}
