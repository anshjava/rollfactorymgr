package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
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
    public List<Roll> findRollByParams(Long id, String sku, RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal mainValue) throws WebServiceException {
        return localRollRegistry.stream()
                .filter(r -> String.valueOf(r.getId()).contains((Optional.ofNullable(id).map(String::valueOf)).orElse(String.valueOf(r.getId()))))
                .filter(r -> r.getSku().contains(Optional.ofNullable(sku).orElse(r.getSku())))
                .filter(r -> r.getRollType() == Optional.ofNullable(rollType).orElse(r.getRollType()))
                .filter(r -> r.getPaper() == Optional.ofNullable(paper).orElse(r.getPaper()))
                .filter(r -> r.getWidthType() == Optional.ofNullable(widthType).orElse(r.getWidthType()))
                .filter(r -> r.getCoreType() == Optional.ofNullable(coreType).orElse(r.getCoreType()))
                .filter(r -> r.getMainValue().compareTo(Optional.ofNullable(mainValue).orElse(r.getMainValue())) == 0)
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
            throw new ValidationException("Roll with SKU " + sku + " is already exists!");
        }

        validateCommonRollParams(rollType, paper, widthType, coreType, value);

        List<Roll> foundDuplicate = findRollByParams(null, null, rollType, paper, widthType, coreType, value);
        if (!foundDuplicate.isEmpty()) {
            String duplicateSku = foundDuplicate.get(0).getSku();
            throw new ValidationException("Error while trying create duplicate roll of SKU " + duplicateSku);
        }

    }

    private void validateUpdateRoll(Roll roll) throws ValidationException {
        if (remoteRollRegistry.stream().noneMatch(r -> r.getSku().equals(roll.getSku()))) {
            throw new ValidationException("Roll with SKU " + roll.getSku() + " was not found, there is nothing to update");
        }

        validateIfRollInWorkflow(roll.getSku());

        validateCommonRollParams(roll.getRollType(), roll.getPaper(), roll.getWidthType(), roll.getCoreType(), roll.getMainValue());

        List<Roll> foundDuplicate = findRollByParams(null, null, roll.getRollType(), roll.getPaper(), roll.getWidthType(), roll.getCoreType(), roll.getMainValue());
        if (!foundDuplicate.isEmpty()) {
            String duplicateSku = foundDuplicate.get(0).getSku();
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
        if (rollType == null) {
            throw new ValidationException("Roll type must be specified");
        }

        if (paper == null) {
            throw new ValidationException("Roll paper must be specified");
        }

        if (widthType == null) {
            throw new ValidationException("Roll width must be specified");
        }

        if (coreType == null) {
            throw new ValidationException("Roll core type must be specified");
        }

        if (value == null) {
            throw new ValidationException("Roll main value must be specified");
        }

        if (rollType == RollType.DIAMETER && value.compareTo(coreType.getDiameter()) != 1) {
            throw new ValidationException("Roll diameter can't be equal or less than core diameter");
        }

        if (rollType == RollType.LENGTH && value.signum() == 1) {
            throw new ValidationException("Roll length can't be equal or less than zero");
        }
    }

    private void validateIfRollInWorkflow(String sku) throws ValidationException {
        if (sku.equals("DIA20018")) {
            throw new ValidationException("Roll with sku " + sku + " is in workflow at this moment");
        } else {
            log.info("Roll with sku " + sku + " is not in workflow");
        }
    }
}
