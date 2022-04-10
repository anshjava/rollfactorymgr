package ru.kamuzta.rollfactorymgr.processor;

import com.google.inject.ImplementedBy;
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
import ru.kamuzta.rollfactorymgr.service.webservice.OrderServiceMock;
import ru.kamuzta.rollfactorymgr.service.webservice.RollService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ImplementedBy(RollProcessorImpl.class)
public interface RollProcessor {

    /**
     * Update local cached Registry by values from server
     *
     * @throws WebServiceException
     */
    void updateRegistryFromServer() throws WebServiceException;

    /**
     * Get rolls in active state from local cached Roll Registry
     *
     * @return active rolls
     * @throws WebServiceException
     */
    List<Roll> getActiveRollsLocal();

    /**
     * Find Roll by SKU in local cached Roll Registry
     *
     * @param sku SKU of Roll
     * @return found Roll
     * @throws WebServiceException
     */
    Roll findRollBySku(@NotNull String sku) throws WebServiceException;

    /**
     * Find Roll by id in local cached Roll Registry
     *
     * @param id - id of Roll
     * @return found Roll
     * @throws WebServiceException
     */
    Roll findRollById(@NotNull Long id) throws WebServiceException;

    /**
     * Find Roll by SKU pattern in local cached Roll Registry
     *
     * @param sku SKU/part of SKU of Roll
     * @return list of matched Rolls
     * @throws WebServiceException
     */
    List<Roll> findRollBySkuPattern(@NotNull String sku) throws WebServiceException;

    /**
     * Find Roll by parameters in local cached Roll Registry
     *
     * @param id  part of roll id
     * @param sku  part of roll sku
     * @param rollType  roll manufacturing method
     * @param paper     raw material
     * @param widthType roll width
     * @param coreType  roll core
     * @param value     main parameter(length or diameter)
     * @return list of matched Rolls
     * @throws WebServiceException
     */
    List<Roll> findRollByParams(@Nullable Long id, @Nullable String sku, @Nullable RollType rollType, @Nullable Paper paper, @Nullable WidthType widthType, @Nullable CoreType coreType, @Nullable BigDecimal value) throws WebServiceException;

    /**
     * Create Roll on Server Registry
     *
     * @param sku       SKU of new Roll
     * @param rollType  roll manufacturing method
     * @param paper     raw material
     * @param widthType roll width
     * @param coreType  roll core
     * @param value     main parameter(length or diameter)
     * @return new Roll
     * @throws WebServiceException if validation fail
     */
    Roll createRoll(@NotNull String sku, @NotNull RollType rollType, @NotNull Paper paper, @NotNull WidthType widthType, @NotNull CoreType coreType, @NotNull BigDecimal value) throws WebServiceException, ValidationException;

    /**
     * Remove Roll on Server Registry by SKU
     * @param sku SKU of Roll to remove
     * @return true if success
     * @throws WebServiceException if Roll with specified SKU was not found or there is some orders with this roll
     */
    boolean removeRollBySku(@NotNull String sku) throws WebServiceException, ValidationException;

    /**
     * Update Roll on Server Registry with new parameters
     * @param roll - Roll with same SKU but diffirent parameters
     * @return updated Roll
     * @throws WebServiceException if validation fail or all parameters are equals
     */
    Roll updateRoll(@NotNull Roll roll) throws WebServiceException, ValidationException;


    void validateCreateRoll(String sku, RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws ValidationException;

    void validateUpdateRoll(Roll roll) throws ValidationException;

    void validateRemoveRoll(String sku) throws ValidationException;

    void validateCommonRollParams(RollType rollType, Paper paper, WidthType widthType, CoreType coreType, BigDecimal value) throws ValidationException;

    void validateIfRollInWorkflow(String sku) throws ValidationException;
}
