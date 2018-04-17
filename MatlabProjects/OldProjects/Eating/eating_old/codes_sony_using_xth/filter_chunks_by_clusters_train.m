function [chunks, cluster_density] = filter_chunks_by_clusters_train(chunks, cluster_centers)
    count = length(chunks);
    min_points =  zeros(count,3);
    labels = zeros(count,1);
    closest_clusters = zeros(count, 1);
    cluster_density = zeros(length(cluster_centers), 2);
    

    for i = 1:count
        [~, min_ind] = min(chunks(i).grav(:,1));
        min_point = chunks(i).grav(min_ind, :);
        label = chunks(i).label;
        cc = find_closest_cluster(min_point, cluster_centers);
        
        labels(i) = label;
        min_points(i,:) = min_point;
        closest_clusters(i) = cc;
        
        if label > 0 && label <1000
            cluster_density(cc, 1) = cluster_density(cc, 1) + 1;
        else
            cluster_density(cc, 2) = cluster_density(cc, 2) + 1;
        end
    end
    
    for i = count:-1:1
        cc = closest_clusters(i);
        if cluster_density(cc, 1) == 0
            chunks(i) = [];
        end        
    end    
    
end