package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ru.kamuzta.rollfactorymgr.utils.json.CouldNotDeserializeJsonException;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.*;
import ru.kamuzta.rollfactorymgr.utils.json.JsonUtil;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class RollServiceMock implements RollService {
    private static JsonUtil jsonUtil = JsonUtil.getInstance();
    private List<Roll> remoteRollRegistry = new ArrayList<>(jsonUtil.getListFromJson("rollRegistry.json", Roll.class, CouldNotDeserializeJsonException::new));
    private AtomicLong count = new AtomicLong(remoteRollRegistry.stream().map(Roll::getId).max(Long::compare).orElse(0L));
    private List<Roll> localRollRegistry = new ArrayList<>();

    @Override
    public void updateRegistryFromServer() throws WebServiceException {
        try {
            localRollRegistry.clear();
            localRollRegistry.addAll(remoteRollRegistry);
            count = new AtomicLong(localRollRegistry.stream().map(Roll::getId).max(Long::compare).orElse(0L));
        } catch (CouldNotDeserializeJsonException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Roll findRollBySku(String sku) throws WebServiceException {
        return localRollRegistry.stream().filter(r -> r.getSku().equals(sku))
                .findFirst()
                .orElseThrow(() -> new WebServiceException("Roll with SKU " + sku + " was not found"))
                .clone();
    }

    @Override
    public List<Roll> findRollBySkuPattern(String sku) throws WebServiceException {
        return localRollRegistry.stream().filter(r -> r.getSku().contains(sku)).collect(Collectors.toList());
    }

    @Override
    public List<Roll> findRollByParams(RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws WebServiceException {
        return localRollRegistry.stream()
                .filter(r -> {
                    if (rollType != null) {
                        return r.getRollType() == rollType;
                    } else {
                        return true;
                    }
                })
                .filter(r -> {
                    if (paper != null) {
                        return r.getPaper() == paper;
                    } else {
                        return true;
                    }
                })
                .filter(r -> {
                    if (widthType != null) {
                        return r.getWidthType() == widthType;
                    } else {
                        return true;
                    }
                })
                .filter(r -> {
                    if (coreType != null) {
                        return r.getCoreType() == coreType;
                    } else {
                        return true;
                    }
                })
                .filter(r -> {
                    if (value != null) {
                        return r.getMainValue().compareTo(value) == 0;
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Roll> getLocalRollRegistry() {
        return localRollRegistry;
    }

    @Override
    public Roll createRoll(String sku, RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws WebServiceException {
        try {
            validateCreateRoll(sku, rollType, paper, widthType, coreType, value);
            Roll newRoll = new Roll(count.incrementAndGet(), sku, rollType, paper, widthType, coreType, value);
            remoteRollRegistry.add(newRoll);
            return newRoll.clone();
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean removeRollBySku(String sku) throws WebServiceException {
        try {
            validateRemoveRoll(sku);
            boolean result = remoteRollRegistry.remove(findRollBySku(sku));
            if (result) {
                updateRegistryFromServer();
            }
            return result;
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    @Override
    public Roll updateRoll(Roll roll) throws WebServiceException {
        try {
            validateUpdateRoll(roll);
            remoteRollRegistry.set(remoteRollRegistry.indexOf(findRollBySku(roll.getSku())), roll);
            updateRegistryFromServer();
            return roll.clone();
        } catch (ValidationException e) {
            throw new WebServiceException(e.getMessage(), e);
        }
    }

    private void validateCreateRoll(String sku, RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws ValidationException {
        if (!sku.matches("[A-Za-z0-9]+")) {
            throw new ValidationException("SKU has wrong format! Must contains only A-Z, a-z, 0-9");
        }

        if (localRollRegistry.stream().anyMatch(r -> r.getSku().equals(sku))) {
            throw new ValidationException("Roll with sku " + sku + " is already exists!");
        }

        validateCommonRollParams(rollType, paper, widthType, coreType, value);

        List<Roll> foundDuplicate = findRollByParams(rollType, paper, widthType, coreType, value);
        if (!foundDuplicate.isEmpty()) {
            String duplicateSku = foundDuplicate.stream().findFirst().get().getSku();
            throw new ValidationException("Error while trying create duplicate roll of SKU " + duplicateSku);
        }

    }

    private void validateUpdateRoll(Roll roll) throws ValidationException {
        if (remoteRollRegistry.stream().noneMatch(r -> r.getSku().equals(roll.getSku()))) {
            throw new ValidationException("Roll with SKU " + roll.getSku() + " was not found, there is nothing to update");
        }

        validateIfRollInWorkflow(roll.getSku());

        validateCommonRollParams(roll.getRollType(), roll.getPaper(), roll.getWidthType(), roll.getCoreType(), roll.getMainValue());

        List<Roll> foundDuplicate = findRollByParams(roll.getRollType(), roll.getPaper(), roll.getWidthType(), roll.getCoreType(), roll.getMainValue());
        if (!foundDuplicate.isEmpty()) {
            String duplicateSku = foundDuplicate.stream().findFirst().get().getSku();
            throw new ValidationException("Error while trying create duplicate roll of SKU " + duplicateSku);
        }
    }

    private void validateRemoveRoll(String sku) throws ValidationException {
        if (remoteRollRegistry.stream().noneMatch(r -> r.getSku().equals(sku))) {
            throw new ValidationException("Roll with SKU " + sku + " was not found, there is nothing to remove");
        }
        validateIfRollInWorkflow(sku);
    }

    private void validateCommonRollParams(RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws ValidationException {
        if (rollType == RollType.DIAMETER) {
            if (value.compareTo(coreType.getDiameter()) != 1) {
                throw new ValidationException("Roll diameter can't be equal or less than core diameter");
            }
        }

        if (rollType == RollType.LENGTH) {
            if (value.compareTo(BigDecimal.ZERO) != 1) {
                throw new ValidationException("Roll length can't be equal or less than zero");
            }
        }
    }

    private void validateIfRollInWorkflow(String sku) {
        //do nothing in Mock service
    }
}
