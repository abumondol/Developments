load('procdata');
downsample_factor = 6;
k = 20;
options = statset('MaxIter',1000);
clusters = [];
sub_count = length(procdata);

for sub = 1:sub_count
    fprintf('Cluster for subject %d: ', sub);
    g = [];
    for s = 1:sub_count
        if s==sub
            continue;
        end
        
        sess_count = length(procdata(s).session);
        for sess = 1:sess_count
            d = procdata(s).session(sess).grav;
            d = d(1:downsample_factor:end, 2:4);            
            g = [g;d];
        end
           
        sess_count = length(procdata_noneat(s).session);
        for sess = 1:sess_count
            d = procdata_noneat(s).session(sess).grav;
            d = d(1:downsample_factor:end, 2:4);
            g = [g;d];
        end
    end
    
    l1 = length(g);
    g = g(g(:,1)<=0, :);  %remove data above x = 0    
    l2 = length(g);    
    
    fprintf('Data length: %d, %d\n', l1, l2);    
    [idx, C] = kmedoids(g, k);
    clusters(sub).C = C;
    
end

save('clusters','clusters');