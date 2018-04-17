% finding minimum and maximum distance for each vertex
load('ico');
ico_count = length(ico);


for i=1:ico_count    
    fprintf('Processing %d\n',i);
    
    vertices = ico(i).vertices;    
    neighbors = ico(i).neighbors;
    
    vertex_count = length(vertices);   
    min_radius_euclidean = zeros(vertex_count,1);
    min_radius_cosine = zeros(vertex_count,1);
    
    for j=1:vertex_count        
        nb = neighbors{j, 1};        
        nb_count = length(nb);
        v = vertices(j, :);
        v = repmat(v, nb_count, 1);
        v_nb = vertices(nb, :);
        
        vmid = (v+v_nb)/2;
        mag = sqrt(sum(vmid.*vmid,2)); %normalize to sphere radius
        vmid = vmid./[mag, mag, mag];
        
        d = (v-vmid);
        d = sum(d.*d,2);
        min_radius_euclidean(j,1) = min(d);
        
        d = v.*vmid;
        cos_thetas = sum(d,2);
        d = 1-cos_thetas;
        min_radius_cosine(j,1) = min(d);
        
    end
    
    ico(i).sqr_euclidean_radius_min = min_radius_euclidean; 
    ico(i).cosine_radius_min = min_radius_cosine;         
    
end

save('ico','ico');


