package ru.kamuzta.rollfactorymgr.processor;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.ValidationException;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.order.OrderLine;
import ru.kamuzta.rollfactorymgr.model.order.OrderState;
import ru.kamuzta.rollfactorymgr.model.roll.*;
import ru.kamuzta.rollfactorymgr.service.webservice.OrderService;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Slf4j
public class RollProcessorImpl implements RollProcessor {

    @Inject
    RollService rollService;

    @Inject
    OrderService orderService;

    @Override
    public void updateRegistryFromServer() throws WebServiceException {
        rollService.updateRegistryFromServer();
    }

    @Override
    public List<Roll> getActiveRollsLocal() {
        return rollService.getActiveRollsLocal();
    }

    @Override
    public Roll findRollBySku(@NotNull String sku) throws WebServiceException {
        return rollService.findRollBySku(sku);
    }

    @Override
    public Roll findRollById(@NotNull Long id) throws WebServiceException {
        return rollService.findRollById(id);
    }

    @Override
    public List<Roll> findRollBySkuPattern(@NotNull String sku) throws WebServiceException {
        return rollService.findRollBySkuPattern(sku);
    }

    @Override
    public List<Roll> findRollByParams(@Nullable Long id, @Nullable String sku, @Nullable RollType rollType, @Nullable Paper paper, @Nullable WidthType widthType, @Nullable CoreType coreType, @Nullable BigDecimal value) throws WebServiceException {
        return rollService.findRollByParams(id, sku, rollType, paper, widthType, coreType, value);
    }

    @Override
    public Roll createRoll(@NotNull String sku, @NotNull RollType rollType, @NotNull Paper paper, @NotNull WidthType widthType, @NotNull CoreType coreType, @NotNull BigDecimal value) throws WebServiceException, ValidationException {
        validateCreateRoll(sku, rollType, paper, widthType, coreType, value);
        Roll newRoll = rollService.createRoll(sku, rollType, paper, widthType, coreType, value);
        updateRegistryFromServer();
        return newRoll;
    }

    @Override
    public boolean removeRollBySku(@NotNull String sku) throws WebServiceException, ValidationException {
        validateRemoveRoll(sku);
        boolean result = rollService.removeRollBySku(sku);
        updateRegistryFromServer();
        return result;
    }

    @Override
    public Roll updateRoll(@NotNull Roll roll) throws WebServiceException, ValidationException {
        validateUpdateRoll(roll);
        Roll updatedRoll = rollService.updateRoll(roll);
        updateRegistryFromServer();
        return updatedRoll;
    }

    @Override
    public void validateCreateRoll(String sku, RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws ValidationException {
        if (!sku.matches("[A-Za-z0-9]+")) {
            throw new ValidationException("SKU has wrong format! Must contains only A-Z, a-z, 0-9");
        }

        if (rollService.getActiveRollsLocal().stream().anyMatch(r -> r.getSku().equals(sku))) {
            throw new ValidationException("Roll with SKU " + sku + " is already exists!");
        }

        validateCommonRollParams(rollType, paper, widthType, coreType, value);

        List<Roll> foundDuplicate = rollService.findRollByParams(null, null, rollType, paper, widthType, coreType, value);
        Optional<Roll> optionalRoll = foundDuplicate.stream().findFirst();
        if (optionalRoll.isPresent()) {
            throw new ValidationException("Error while trying create duplicate roll of SKU " + optionalRoll.get().getSku());
        }
    }

    @Override
    public void validateUpdateRoll(Roll roll) throws ValidationException {
        if (rollService.getActiveRollsLocal().stream().noneMatch(r -> r.getSku().equals(roll.getSku()))) {
            throw new ValidationException("Roll with SKU " + roll.getSku() + " was not found, there is nothing to update");
        }

        validateCommonRollParams(roll.getRollType(), roll.getPaper(), roll.getWidthType(), roll.getCoreType(), roll.getMainValue());

        validateIfRollInWorkflow(roll.getSku());

        List<Roll> foundDuplicate = findRollByParams(null, null, roll.getRollType(), roll.getPaper(), roll.getWidthType(), roll.getCoreType(), roll.getMainValue());
        Optional<Roll> optionalRoll = foundDuplicate.stream().findFirst();
        if (optionalRoll.isPresent()) {
            throw new ValidationException("Error while trying create duplicate roll of SKU " + optionalRoll.get().getSku());
        }
    }

    @Override
    public void validateRemoveRoll(String sku) throws ValidationException {
        if (rollService.getActiveRollsLocal().stream().noneMatch(r -> r.getSku().equals(sku))) {
            throw new ValidationException("Roll with SKU " + sku + " was not found, there is nothing to remove");
        }
        validateIfRollInWorkflow(sku);
    }

    @Override
    public void validateCommonRollParams(RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws ValidationException {
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

        if (rollType == RollType.DIAMETER && value.compareTo(coreType.getDiameter()) <= 0) {
            throw new ValidationException("Roll diameter can't be equal or less than core diameter");
        }

        if (rollType == RollType.LENGTH && value.signum() <= 0) {
            throw new ValidationException("Roll length can't be equal or less than zero");
        }
    }

    @Override
    public void validateIfRollInWorkflow(String sku) throws ValidationException {
        boolean isSkuInProgress = orderService.getLocalRegistry().stream()
                .filter(order -> !EnumSet.of(OrderState.COMPLETED, OrderState.CANCELED).contains(order.getState()))
                .flatMap(order -> order.getLines().stream())
                .filter(orderLine -> !EnumSet.of(OrderState.COMPLETED, OrderState.CANCELED).contains(orderLine.getState()))
                .map(OrderLine::getRoll).map(Roll::getSku)
                .anyMatch(skuInProgress -> skuInProgress.equals(sku));
        if (isSkuInProgress) {
            throw new ValidationException("Roll with sku " + sku + " is in workflow at this moment");
        } else {
            log.info("Roll with sku " + sku + " is not in workflow");
        }
    }
}
