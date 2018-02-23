package com.unciv.logic.city;

import com.badlogic.gdx.utils.Predicate;
import com.unciv.logic.map.TileInfo;
import com.unciv.models.linq.Linq;

public class CityExpansionManager{

    transient public CityInfo cityInfo;
    public int cultureStored;
    private int tilesClaimed;

    private void addNewTileWithCulture(){
        cultureStored -= getCultureToNextTile();
        tilesClaimed++;

        for (int i = 2; i <4 ; i++) {
            Linq<TileInfo> tiles = cityInfo.civInfo.gameInfo.tileMap.getTilesInDistance(cityInfo.cityLocation,i);
            tiles = tiles.where(new Predicate<TileInfo>() {
                @Override
                public boolean evaluate(TileInfo arg0) {
                    return arg0.owner == null;
                }
            });
            if(tiles.size()==0) continue;

            TileInfo TileChosen=null;
            double TileChosenRank=0;
            for(TileInfo tile : tiles){
                double rank = cityInfo.rankTile(tile);
                if(rank>TileChosenRank){
                    TileChosenRank = rank;
                    TileChosen = tile;
                }
            }
            if(TileChosen==null) // We can't add any more tiles to this city!
                return;
            TileChosen.owner = cityInfo.civInfo.civName;
            return;
        }
    }

    public int getCultureToNextTile(){
        // This one has conflicting sources -
        // http://civilization.wikia.com/wiki/Mathematics_of_Civilization_V says it's 20+(10(t-1))^1.1
        // https://www.reddit.com/r/civ/comments/58rxkk/how_in_gods_name_do_borders_expand_in_civ_vi/ has it
        //   (per game XML files) at 6*(t+0.4813)^1.3
        // The second seems to be more based, so I'll go with that
        double a = 6*Math.pow(tilesClaimed+1.4813,1.3);
        if(cityInfo.civInfo.getBuildingUniques().contains("NewTileCostReduction")) a *= 0.75; //Speciality of Angkor Wat
        if(cityInfo.civInfo.policies.isAdopted("Tradition")) a *= 0.75;
        return (int)Math.round(a);
    }

    public void nextTurn(float culture) {

        cultureStored+=culture;
        if(cultureStored>=getCultureToNextTile()){
            addNewTileWithCulture();
            cityInfo.civInfo.gameInfo.addNotification(cityInfo.name+" has expanded its borders!",cityInfo.cityLocation);
        }
    }

    public int getGoldCostOfTile(TileInfo tileInfo){
        //int distanceFromCenter =
        return 50;
    }
}