public enum Item{
    GOLD("Gold"),
    COAL("Coal"),
    LOG("Log"),
    IRON_ORE("Iron Ore"),
    WHEAT("Wheat"),
    FISH("Fish"),
    DIAMOND("Diamond"),
    COW("Cow"),
    OIL("Oil"),
    IRON("Iron"),
    STEEL("Steel"),
    LUMBER("Lumber"),
    SCRAP_WOOD("Scrap Wood"),
    PISTOL("Semi-Auto Pistol"),
    RIFLE("Fully Automatic Rifle"),
    REVOLVER("Revolver"),
    SWORD("Sword"),
    AXE("Axe"),
    PICKAXE("Pickaxe"),
    SHOVEL("Shovel"),
    PAPER("Paper"),
    JEWELRY("Jewelry"),
    LEATHER("Leather"),
    COOKED_FISH("Cooked Fish"),
    COTTON("Cotton"),
    FURNITURE("Furniture"),
    BEEF("Beef"),
    BOOK("Book"),
    STEAK("Steak"),
    BREAD("Bread"),
    HELMET("Helmet"),
    CHESTPLATE("Chestplate"),
    LEGGINGS("Leggings"),
    FUEL("Fuel"),
    PLASTIC("Plastic"),
    CLAY("Clay"),
    POTTERY("Pottery"),
    SAND("Sand"),
    GLASS("Glass"),
    BOOTS("Boots"),
    HOE("Hoe"),
    STONE("Rock"),
    DIRT("Dirt");
    private final String name;
    private Item(String name){
        this.name = name;
    }
    @Override
    public String toString(){
        return name;
    }
}