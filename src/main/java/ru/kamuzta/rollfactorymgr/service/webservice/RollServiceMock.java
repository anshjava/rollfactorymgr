package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.model.client.Client;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class RollServiceMock implements RollService {
    private static JsonUtil jsonUtil = JsonUtil.getInstance();
    private final List<Roll> remoteRollRegistry = new ArrayList<>(jsonUtil.getListFromJson("rollRegistry.json", Roll.class, CouldNotDeserializeJsonException::new));
    private AtomicLong count = new AtomicLong(remoteRollRegistry.stream().map(Roll::getId).max(Long::compare).orElse(0L));
    private final List<Roll> localRollRegistry = new ArrayList<>();

    @Override
    public void updateRegistryFromServer() throws WebServiceException {
        localRollRegistry.clear();
        localRollRegistry.addAll(remoteRollRegistry);
        count = new AtomicLong(remoteRollRegistry.stream().map(Roll::getId).max(Long::compare).orElse(0L));
    }

    @Override
    public List<Roll> getActiveRollsLocal() {
        return localRollRegistry.stream()
                .filter(r -> r.getState() == RollState.ACTIVE)
                .map(Roll::new).collect(Collectors.toList());
    }

    @Override
    public Roll findRollBySku(@NotNull String sku) throws WebServiceException {
        return localRollRegistry.stream().filter(r -> r.getSku().equals(sku))
                .filter(r -> r.getState() == RollState.ACTIVE)
                .findFirst()
                .map(Roll::new)
                .orElseThrow(() -> new WebServiceException("Roll with SKU " + sku + " was not found"));
    }

    @Override
    public List<Roll> findRollBySkuPattern(@NotNull String sku) throws WebServiceException {
        return localRollRegistry.stream()
                .filter(r -> r.getState() == RollState.ACTIVE)
                .filter(r -> r.getSku().contains(sku))
                .map(Roll::new)
                .collect(Collectors.toList());
    }

    @Override
    public Roll findRollById(@NotNull Long id) throws WebServiceException {
        return localRollRegistry.stream().filter(r -> r.getId().equals(id))
                .filter(r -> r.getState() == RollState.ACTIVE)
                .findFirst()
                .map(Roll::new)
                .orElseThrow(() -> new WebServiceException("Roll with id " + id + " was not found"));
    }

    @Override
    public List<Roll> findRollByParams(@Nullable Long id, @Nullable String sku, @Nullable RollType rollType, @Nullable Paper paper, @Nullable WidthType widthType, @Nullable CoreType coreType, @Nullable BigDecimal mainValue) throws WebServiceException {
        return localRollRegistry.stream()
                .filter(r -> r.getState() == RollState.ACTIVE)
                .filter(r -> String.valueOf(r.getId()).contains((Optional.ofNullable(id).map(String::valueOf)).orElse(String.valueOf(r.getId()))))
                .filter(r -> r.getSku().contains(Optional.ofNullable(sku).orElse(r.getSku())))
                .filter(r -> r.getRollType() == Optional.ofNullable(rollType).orElse(r.getRollType()))
                .filter(r -> r.getPaper() == Optional.ofNullable(paper).orElse(r.getPaper()))
                .filter(r -> r.getWidthType() == Optional.ofNullable(widthType).orElse(r.getWidthType()))
                .filter(r -> r.getCoreType() == Optional.ofNullable(coreType).orElse(r.getCoreType()))
                .filter(r -> r.getMainValue().compareTo(Optional.ofNullable(mainValue).orElse(r.getMainValue())) == 0)
                .map(Roll::new).collect(Collectors.toList());
    }

    @Override
    public Roll createRoll(@NotNull String sku, @NotNull RollType rollType, @NotNull Paper paper, @NotNull WidthType widthType, @NotNull CoreType coreType, @NotNull BigDecimal value) throws WebServiceException {
        Roll newRoll = new Roll(count.incrementAndGet(), sku, rollType, paper, widthType, coreType, value, RollState.ACTIVE);
        remoteRollRegistry.add(newRoll);
        updateRegistryFromServer();
        return new Roll(newRoll);
    }

    @Override
    public boolean removeRollBySku(@NotNull String sku) throws WebServiceException {
        Roll rollToDelete = findRollBySku(sku);
        rollToDelete.setState(RollState.DELETED);
        updateRoll(rollToDelete);
        updateRegistryFromServer();
        return true;
    }

    @Override
    public Roll updateRoll(@NotNull Roll roll) throws WebServiceException {
        Roll oldRoll = findRollById(roll.getId());
        remoteRollRegistry.set(remoteRollRegistry.indexOf(oldRoll), roll);
        updateRegistryFromServer();
        return new Roll(roll);
    }
}
