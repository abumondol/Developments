function xyz = pca_get_train(gen_data, sub_id)
    
    xyz = [];
    for i =1:10
        if i==sub_id
            continue
        end
        w = gen_data(i).xyz_hw;
        w = w(w(:, end)==1, 1:3);
        xyz = [xyz;w];
    end  
    
    %coeff = pca(xyz);
    %xyz_new = xyz*coeff;

end   