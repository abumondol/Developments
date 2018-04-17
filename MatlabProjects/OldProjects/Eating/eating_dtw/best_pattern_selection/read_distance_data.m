srcFolder = 'C:/ASM/developments/JavaProjects/Eating Analysis DTW/myresults/dist_acn_cosine/';

distances = [];
for i=0:35
    fprintf('Reading subject %d\n', i);
    
    filename = strcat(srcFolder,'pos_pos_', num2str(i),'.csv');
    d = csvread(filename);
    d(:,1) = d(:,1) +1;
    d(:,2) = d(:,2) +1;
    d(:,3) = d(:,3) +1;    
    d(:,6) = d(:,6) +1;
    d(:,7) = d(:,7) +1;
    d(:,8) = d(:,8) +1;    
    distances(i+1).pos_pos = d;
    
    filename = strcat(srcFolder,'pos_neg_', num2str(i),'.csv');
    d = csvread(filename);
    d(:,1) = d(:,1) +1;
    d(:,2) = d(:,2) +1;
    d(:,3) = d(:,3) +1;    
    d(:,6) = d(:,6) +1;
    d(:,7) = d(:,7) +1;
    d(:,8) = d(:,8) +1;    
    distances(i+1).pos_neg = d;
    
    
    filename = strcat(srcFolder,'pos_neg2_', num2str(i),'.csv');
    d = csvread(filename);
    d(:,1) = d(:,1) +1;
    d(:,2) = d(:,2) +1;
    d(:,3) = d(:,3) +1;    
    d(:,6) = d(:,1) +1;
    d(:,7) = d(:,2) +1;
    d(:,8) = d(:,3) +1;    
    distances(i+1).pos_neg2 = d;
    
end

save('distances', 'distances');