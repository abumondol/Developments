window_size = 50;
step_size = 10;

sub_count = length(hw);
for i=1:sub_count    
    episode_count = length(hw(i).data);
    
    for j= 1:episode_count
        fprintf('Extracting features: subject: %d, episode: %d\n',i, j);
        t = hw(i).data(j).alg(:, 1); 
        grav = hw(i).data(j).alg(:, 8:10);
        grav = round(grav*10, 0); 
        length(grav)
        
        grav1 = [grav; 0 0 0];        
        grav2 = [0 0 0; grav];               
        d = sum(grav1 - grav2, 2);
        d(end,:)=[];        
        grav = grav(d~=0, :);               
        
        grav_diff = [grav; grav(end,:)] - [grav(1,:); grav];
        grav_diff(end,:) = [];
        
        total_points = length(grav);
        feature_count = floor( (total_points-window_size)/step_size + 1);
        features = zeros(feature_count, 16);
        
        k=1;
        for s = 1 : step_size : total_points-window_size+1            
            e = s+window_size-1;
            m = s + (window_size)/2 -1;
            features(k,:) = [grav(m,:), grav(s,:), grav(e,:), mean(grav_diff(s:m, :)), mean(grav_diff(m+1:e, :)), t(e)-t(s)];
            k=k+1;
        end
        
        k=k-1;        
        if k~=feature_count
            fprintf('Error: k= %d, feature_count= %d, i=%d, j=%d\n',k,feature_count,i, j);
            return
        end
        features([1,end],:)=[];
        hw(i).data(j).features = features;
    end
end

save('hw','hw');

