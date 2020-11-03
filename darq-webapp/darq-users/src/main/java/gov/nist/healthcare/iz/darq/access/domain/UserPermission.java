package gov.nist.healthcare.iz.darq.access.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import gov.nist.healthcare.iz.darq.users.domain.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonSerialize(using = UserPermissionSerializer.class)
public class UserPermission extends HashMap<QualifiedScope, Map<ResourceType, Map<QualifiedAccessToken, Set<Action>>>> {

    private final Set<Permission> permissions = new HashSet<>();
    private final Set<String> facilities = new HashSet<>();

    public Set<String> getFacilities() {
        return Collections.unmodifiableSet(facilities);
    }
    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }

    public boolean hasPermissions(Set<Permission> permissions) {
        return this.permissions.containsAll(permissions);
    }

    public boolean hasActionsFor(QualifiedScope scope, ResourceType resourceType, QualifiedAccessToken qualifiedAccessToken, Set<Action> actions) {
        Map<ResourceType, Map<QualifiedAccessToken, Set<Action>>> byScope = this.get(scope);
        if(byScope != null) {
            Map<QualifiedAccessToken, Set<Action>> byResourceType = byScope.get(resourceType);
            if(byResourceType != null) {
                Set<Action> any = byResourceType.getOrDefault(QualifiedAccessToken.ANY, Collections.emptySet());
                Set<Action> byAccessToken = byResourceType.getOrDefault(qualifiedAccessToken, Collections.emptySet());

                return Stream.concat(any.stream(), byAccessToken.stream()).collect(Collectors.toSet()).containsAll(actions);
            }
        }
        return false;
    }

    public boolean hasActionFor(QualifiedScope scope, ResourceType resourceType, QualifiedAccessToken qualifiedAccessToken, Action action) {
        Map<ResourceType, Map<QualifiedAccessToken, Set<Action>>> byScope = this.get(scope);
        if(byScope != null) {
            Map<QualifiedAccessToken, Set<Action>> byResourceType = byScope.get(resourceType);
            if(byResourceType != null) {
                Set<Action> any = byResourceType.getOrDefault(QualifiedAccessToken.ANY, Collections.emptySet());
                Set<Action> byAccessToken = byResourceType.getOrDefault(qualifiedAccessToken, Collections.emptySet());

                return Stream.concat(any.stream(), byAccessToken.stream()).collect(Collectors.toSet()).contains(action);
            }
        }
        return false;
    }

    public UserPermission put(QualifiedScope scope, ResourceType resourceType, QualifiedAccessToken qualifiedAccessToken, Action[] actions) {
        Set<Action> mapped = this
                .computeIfAbsent(scope, (key) -> new HashMap<>())
                .computeIfAbsent(resourceType, (key) -> new HashMap<>())
                .computeIfAbsent(qualifiedAccessToken, (key) -> new HashSet<>());
        mapped.addAll(Arrays.asList(actions));
        return this;
    }

    public UserPermission putGlobalPermission(Permission permission, User user) {
        this.permissions.add(permission);
        QualifiedScope qualifiedScope = new QualifiedScope(Scope.GLOBAL);
        Arrays.stream(permission.scopes).filter((scope) -> scope.getScope().equals(Scope.GLOBAL)).forEach((scope) -> {
            Arrays.stream(scope.getActions()).forEach((resourceTokenAction -> {
                Arrays.stream(resourceTokenAction.getTokenActions()).forEach((tokenAction -> {
                    QualifiedAccessToken qualifiedAccessToken = new QualifiedAccessToken(tokenAction.getToken(), user);
                    this.put(qualifiedScope, resourceTokenAction.getResourceType(), qualifiedAccessToken, tokenAction.getActions());
                }));
            }));
        });
        return this;
    }

    public UserPermission putFacilityPermission(Permission permission, User user, String facilityQualifier) {
        this.permissions.add(permission);
        this.facilities.add(facilityQualifier);
        QualifiedScope qualifiedScope = new QualifiedScope(Scope.FACILITY, facilityQualifier);
        Arrays.stream(permission.scopes).filter((scope) -> scope.getScope().equals(Scope.FACILITY)).forEach((scope) -> {
            Arrays.stream(scope.getActions()).forEach((resourceTokenAction -> {
                Arrays.stream(resourceTokenAction.getTokenActions()).forEach((tokenAction -> {
                    QualifiedAccessToken qualifiedAccessToken = new QualifiedAccessToken(tokenAction.getToken(), user);
                    this.put(qualifiedScope, resourceTokenAction.getResourceType(), qualifiedAccessToken, tokenAction.getActions());
                }));
            }));
        });
        return this;
    }
}
