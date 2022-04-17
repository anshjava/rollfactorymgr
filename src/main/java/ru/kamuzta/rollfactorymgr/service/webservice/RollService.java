package ru.kamuzta.rollfactorymgr.service.webservice;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.kamuzta.rollfactorymgr.exception.WebServiceException;
import ru.kamuzta.rollfactorymgr.model.roll.*;

import java.math.BigDecimal;
import java.util.List;

@ImplementedBy(RollServiceMock.class)
public interface RollService {
    /**
     * Update local cached Registry by values from server
     *
     * @throws WebServiceException on connection problems
     */
    void updateRegistryFromServer() throws WebServiceException;

    /**
     * Get rolls in active state from local cached Roll Registry
     *
     * @return active rolls
     * @throws WebServiceException on connection problems
     */
    List<Roll> getActiveRollsLocal();

    /**
     * Find Roll by SKU in local cached Roll Registry
     *
     * @param sku SKU of Roll
     * @return found Roll
     * @throws WebServiceException on connection problems
     */
    Roll findRollBySku(@NotNull String sku) throws WebServiceException;

    /**
     * Find Roll by id in local cached Roll Registry
     *
     * @param id - id of Roll
     * @return found Roll
     * @throws WebServiceException on connection problems
     */
    Roll findRollById(@NotNull Long id) throws WebServiceException;

    /**
     * Find Roll by SKU pattern in local cached Roll Registry
     *
     * @param sku SKU/part of SKU of Roll
     * @return list of matched Rolls
     * @throws WebServiceException on connection problems
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
     * @throws WebServiceException on connection problems
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
     * @throws WebServiceException on connection problems or remote validation fail
     */
    Roll createRoll(@NotNull String sku, @NotNull RollType rollType, @NotNull Paper paper, @NotNull WidthType widthType, @NotNull CoreType coreType, @NotNull BigDecimal value) throws WebServiceException;

    /**
     * Remove Roll on Server Registry by SKU
     * @param sku SKU of Roll to remove
     * @return true if success
     * @throws WebServiceException on connection problems or remote validation fail
     */
    boolean removeRollBySku(@NotNull String sku) throws WebServiceException;

    /**
     * Update Roll on Server Registry with new parameters
     * @param roll - Roll with same SKU but diffirent parameters
     * @return updated Roll
     * @throws WebServiceException on connection problems or remote validation fail
     */
    Roll updateRoll(@NotNull Roll roll) throws WebServiceException;

}
