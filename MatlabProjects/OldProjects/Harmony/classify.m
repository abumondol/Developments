function classify(features)
load('harmony_features'); % feature_instances_pos and feature_instances_neg

pos = feature_instances_pos;
neg = feature_instances_neg(:,features);
res=[];
for k = 25:25
    train_pos = pos( pos(:,end)<=5, features ); 
    test_pos = pos( pos(:,end) > 5, features );
        
    [idx, C, sumd, D] = kmeans(train_pos, k);       
  
    radius = find_radius(idx, C, D);
    [TP, TN, FP, FN, assign_count] = verify(C, radius, test_pos, neg);
    assign_count = [find_cluster_sizes(idx,k), assign_count];
    P = TP/(TP+FP);
    R = TP/(TP+FN);
    F = 2*P*R/(P+R);
    res = [res; k, TP, TN, FP, FN, P, R, F];
    dlmwrite('C.csv',C);
    dlmwrite('radius.csv',radius');
    dlmwrite('assign_count.csv', assign_count);
end

for i=1:25
    
end

format long g
res
save('res','res');

end

function radius = find_radius(idx, C, D)
    k = size(C,1);
    radius = zeros(k, 1);
    for i=1:k
        a = D(idx(:,1)==i,i);
        radius(i,1) = max(a);        
    end    
end

function [TP, TN, FP, FN, assign_count] = verify(C, radius, test_pos, neg)

    TP = 0;
    TN = 0; 
    FP = 0;
    FN = 0;
    k = size(C,1);
   assign_count = zeros(k,2);
    len = size(test_pos,1);
    for i = 1:len
        res = 0;
        for j=1:k                        
            d = test_pos(i,:) - C(j,:);
            d = sum(d.*d, 2);
            if d < radius(j,1)
                res = j;
                break;
            end
        end
        
        if res == 0
            FN = FN + 1;
        else
            TP = TP + 1;
            assign_count(j,1) = assign_count(j,1)+1;
        end        
    end
    
    
    len = size(neg,1);
    for i = 1:len
        res = 0;
        for j=1:k
            d = neg(i,:) - C(j,:);
            d = sum(d.*d, 2);
            if d < radius(j,1)
                res = j;
                break;
            end
        end
        
        if res == 0
            TN = TN + 1;
        else
            assign_count(j,2) = assign_count(j,2)+1;
            FP = FP + 1;
        end        
    end    
end

function cluster_sizes = find_cluster_sizes(idx, k)
    cluster_sizes = zeros(k,1);
    for i=1:k
        cluster_sizes(i,1) = sum(idx(:,1)==i);
    end
end