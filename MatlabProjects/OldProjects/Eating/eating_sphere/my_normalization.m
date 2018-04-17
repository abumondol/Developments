function n = my_normalization(a)
     m = sqrt(sum(a.*a, 2));
     n = a./[m, m, m];
end