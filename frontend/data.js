// Product Catalog Data for Apna Store - Royal Couture & Indian Ethnic Wear

export const CATEGORIES = [
  { id: 'all', name: 'All Collection', count: 14 },
  { id: 'ethnic-women', name: "Women's Royal Ethnic", count: 5 },
  { id: 'ethnic-men', name: "Men's Royal Sherwanis", count: 4 },
  { id: 'outerwear', name: 'Luxury Outerwear', count: 3 },
  { id: 'jewelry', name: 'Royal Jewelry', count: 2 }
];

export const PRODUCTS = [
  {
    id: 'prod-ind-01',
    name: 'Royal Zardozi Silk Bridal Lehenga',
    category: 'ethnic-women',
    gender: 'women',
    price: 1050,
    originalPrice: 1250,
    rating: 5.0,
    reviewCount: 64,
    badge: 'Royal Heritage',
    image: './assets/royal_lehenga.png',
    secondaryImage: 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80',
    description: 'A masterpiece of royal heritage crafted in pure crimson mulberry raw silk, lavishly embroidered with hand-drawn gold zardozi, dabka, and pearl embellishments.',
    details: [
      '100% Pure Raw Mulberry Silk with Double Net Dupatta',
      'Handcrafted zardozi, gold zari, and pearl work',
      'Custom waist tailoring with latkan tassels',
      'Includes padded blouse piece and can-can underskirt layering'
    ],
    sizes: ['XS', 'S', 'M', 'L', 'Custom'],
    colors: [
      { name: 'Royal Maroon', hex: '#4A0E17' },
      { name: 'Deep Emerald', hex: '#0B3B24' },
      { name: 'Imperial Gold', hex: '#D4AF37' }
    ],
    inStock: true,
    stockCount: 4,
    isFeatured: true
  },
  {
    id: 'prod-ind-02',
    name: 'Imperial Velvet Zardozi Wedding Sherwani',
    category: 'ethnic-men',
    gender: 'men',
    price: 880,
    originalPrice: 1050,
    rating: 4.9,
    reviewCount: 48,
    badge: 'Groom Collection',
    image: 'https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80',
    secondaryImage: 'https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=800&q=80',
    description: 'High-collared royal velvet sherwani detailed with regal tone-on-tone French knots and metallic threadwork. Accompanied by a pleated silk churidar and stole.',
    details: [
      'Deep Royal Navy Micro-Velvet',
      'Hand-cut antique brass crest buttons',
      'Includes silk Churidar & embroidered Dupatta stole',
      'Specialist dry clean only'
    ],
    sizes: ['S', 'M', 'L', 'XL', 'XXL'],
    colors: [
      { name: 'Imperial Navy', hex: '#0D1B2A' },
      { name: 'Royal Ivory', hex: '#FDFBF7' },
      { name: 'Wine Velvet', hex: '#3B111A' }
    ],
    inStock: true,
    stockCount: 6,
    isFeatured: true
  },
  {
    id: 'prod-ind-03',
    name: 'Kanjivaram Pure Silk Gold Zari Saree',
    category: 'ethnic-women',
    gender: 'women',
    price: 550,
    originalPrice: 680,
    rating: 5.0,
    reviewCount: 52,
    badge: 'Handloom',
    image: 'https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=800&q=80',
    secondaryImage: 'https://images.unsplash.com/photo-1583391733956-6c78276477e2?auto=format&fit=crop&w=800&q=80',
    description: 'Traditional Kanjivaram silk saree hand-woven by master weavers in Tamil Nadu, featuring temple motif gold zari borders and rich pallu.',
    details: [
      '100% Certified Mulberry Handloom Silk',
      'Pure silver-gold tested zari weaving',
      'Unstitched matching blouse piece included (80cm)',
      'Silk Mark certified product'
    ],
    sizes: ['Free Size'],
    colors: [
      { name: 'Crimson Red', hex: '#990000' },
      { name: 'Peacock Blue', hex: '#005F73' },
      { name: 'Mustard Gold', hex: '#E0A96D' }
    ],
    inStock: true,
    stockCount: 8,
    isFeatured: true
  },
  {
    id: 'prod-ind-04',
    name: 'Emerald Velvet Gota Patti Anarkali Suit',
    category: 'ethnic-women',
    gender: 'women',
    price: 460,
    originalPrice: 540,
    rating: 4.8,
    reviewCount: 39,
    badge: 'Trending',
    image: 'https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=800&q=80',
    secondaryImage: 'https://images.unsplash.com/photo-1595777457583-95e059d581b8?auto=format&fit=crop&w=800&q=80',
    description: 'Floor-length flared velvet Anarkali featuring intricate Rajasthani gota patti handwork along the hemline, neck, and organza dupatta.',
    details: [
      'Plush Silk Velvet with Organza Dupatta',
      'Handcrafted Gota Patti & Mirror Work',
      'Comes with stretch lycra leggings',
      'Dry clean recommended'
    ],
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    colors: [
      { name: 'Royal Emerald', hex: '#004B23' },
      { name: 'Ruby Pink', hex: '#A4133C' }
    ],
    inStock: true,
    stockCount: 9,
    isFeatured: false
  },
  {
    id: 'prod-ind-05',
    name: 'Raw Silk Nehru Jacket & Kurta Set',
    category: 'ethnic-men',
    gender: 'men',
    price: 290,
    originalPrice: 350,
    rating: 4.9,
    reviewCount: 31,
    badge: 'Festive Classic',
    image: 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=800&q=80',
    secondaryImage: 'https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=800&q=80',
    description: 'Structure raw silk mandarin collared sleeveless jacket featuring botanical thread embroidery, layered over a crisp cotton-silk kurta pajama.',
    details: [
      'Raw Silk Jacket, Cotton Silk Kurta Set',
      'Fabric-covered metallic buttons',
      'Mandarin collar with welt pocket',
      'Machine wash gentle / dry clean'
    ],
    sizes: ['S', 'M', 'L', 'XL'],
    colors: [
      { name: 'Cream & Gold', hex: '#EEDC9A' },
      { name: 'Royal Navy', hex: '#1D2A44' }
    ],
    inStock: true,
    stockCount: 14,
    isFeatured: false
  },
  {
    id: 'prod-ind-06',
    name: 'Antique Kundan & Pearl Heritage Necklace',
    category: 'jewelry',
    gender: 'women',
    price: 670,
    originalPrice: 790,
    rating: 5.0,
    reviewCount: 41,
    badge: 'Artisanal Jewelry',
    image: 'https://images.unsplash.com/photo-1630019852942-f89202989a59?auto=format&fit=crop&w=800&q=80',
    secondaryImage: 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?auto=format&fit=crop&w=800&q=80',
    description: 'Handcrafted Meenakari Kundan choker necklace set embedded with natural freshwater pearls and ruby drop beads.',
    details: [
      '22K Gold Foil Plated Brass Base',
      'Uncut glass Kundan stones & cultured pearls',
      'Includes matching Jhumka earrings and Maang Tikka',
      'Store in airtight velvet pouch provided'
    ],
    sizes: ['Adjustable Choker'],
    colors: [
      { name: 'Kundan Gold', hex: '#D4AF37' }
    ],
    inStock: true,
    stockCount: 7,
    isFeatured: true
  },
  {
    id: 'prod-01',
    name: 'Apna Store Silk Trench Coat',
    category: 'outerwear',
    gender: 'women',
    price: 480,
    originalPrice: 590,
    rating: 4.9,
    reviewCount: 42,
    badge: 'Bestseller',
    image: './assets/silk_blazer.png',
    secondaryImage: 'https://images.unsplash.com/photo-1548883354-7622d03aca27?auto=format&fit=crop&w=800&q=80',
    description: 'Masterfully tailored from pure mulberry silk blend, featuring structured shoulder lines, custom brass buttons, and a cinched waist belt.',
    details: [
      '70% Mulberry Silk, 30% Fine Wool',
      'Double-breasted front closure',
      'Water-repellent finish',
      'Dry clean only'
    ],
    sizes: ['XS', 'S', 'M', 'L', 'XL'],
    colors: [
      { name: 'Onyx Black', hex: '#111111' },
      { name: 'Champagne Silk', hex: '#E5D3B3' }
    ],
    inStock: true,
    stockCount: 8,
    isFeatured: false
  },
  {
    id: 'prod-02',
    name: 'Midnight Velvet Gala Evening Gown',
    category: 'ethnic-women',
    gender: 'women',
    price: 650,
    originalPrice: 720,
    rating: 5.0,
    reviewCount: 28,
    badge: 'Limited Edition',
    image: './assets/velvet_dress.png',
    secondaryImage: 'https://images.unsplash.com/photo-1566174053879-31528523f8ae?auto=format&fit=crop&w=800&q=80',
    description: 'An enchanting floor-length velvet gown with a delicate back plunge, hand-embroidered metallic accents, and a fluid silhouette.',
    details: [
      '100% Deep Velvet Cotton Silk',
      'Concealed back zip closure',
      'Made in Paris Atelier'
    ],
    sizes: ['XS', 'S', 'M', 'L'],
    colors: [
      { name: 'Midnight Blue', hex: '#0F1B29' }
    ],
    inStock: true,
    stockCount: 5,
    isFeatured: false
  },
  {
    id: 'prod-03',
    name: 'Signature Lambskin Moto Jacket',
    category: 'outerwear',
    gender: 'men',
    price: 520,
    originalPrice: 600,
    rating: 4.8,
    reviewCount: 56,
    badge: 'Iconic',
    image: './assets/leather_jacket.png',
    secondaryImage: 'https://images.unsplash.com/photo-1521223890158-f9f7c3d5d504?auto=format&fit=crop&w=800&q=80',
    description: 'Crafted from butter-soft full-grain lambskin with heavy brushed silver hardware, quilted shoulder panels, and asymmetric zip.',
    details: [
      '100% Italian Lambskin Leather',
      '100% Cupro lining',
      'YKK silver zippers'
    ],
    sizes: ['S', 'M', 'L', 'XL'],
    colors: [
      { name: 'Obsidian Black', hex: '#0D0D0D' }
    ],
    inStock: true,
    stockCount: 12,
    isFeatured: false
  }
];

export const PROMO_CODES = {
  'ROYAL20': { discount: 0.20, code: 'ROYAL20', description: '20% Off Royal Heritage Collection' },
  'APNA20': { discount: 0.20, code: 'APNA20', description: '20% Off Launch Special' },
  'LUMIERE20': { discount: 0.20, code: 'LUMIERE20', description: '20% Off Launch Special' },
  'WELCOME10': { discount: 0.10, code: 'WELCOME10', description: '10% Off First Order' },
  'VIP50': { discount: 50, fixed: true, code: 'VIP50', description: '₹50 Flat Off Orders over ₹300' }
};

export const REVIEWS = [
  {
    id: 1,
    author: 'Priya Sharma',
    role: 'Verified Buyer',
    avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80',
    rating: 5,
    comment: 'The Royal Zardozi Bridal Lehenga is breathtaking! The hand embroidery detail and mulberry raw silk weight feel regal. Best wedding purchase.',
    date: 'Yesterday',
    product: 'Royal Zardozi Silk Bridal Lehenga'
  },
  {
    id: 2,
    author: 'Rohan Verma',
    role: 'Groom',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=120&q=80',
    rating: 5,
    comment: 'Wore the Imperial Velvet Sherwani for my reception. The brass crest buttons and churidar fit were tailored to perfection.',
    date: '3 days ago',
    product: 'Imperial Velvet Zardozi Wedding Sherwani'
  },
  {
    id: 3,
    author: 'Ananya Roy',
    role: 'Verified Buyer',
    avatar: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?auto=format&fit=crop&w=120&q=80',
    rating: 5,
    comment: 'The Kundan & Pearl Heritage necklace is heavy, authentic, and gleams beautifully. Shipped in a premium velvet gift box.',
    date: '1 week ago',
    product: 'Antique Kundan & Pearl Heritage Necklace'
  }
];
