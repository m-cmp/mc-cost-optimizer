import {
  getDisplayItemWithVendorBaseOnViewBy, getDisplayItemBaseOnViewBy
} from '@/util/formatAccountUtils';

// Unit test for format account utils
describe('Format Account Utils', () => {
  it('Get display item base on view by', () => {
    const account = {
      item: "216093335544",
      itemAlias: "prod-account",
      order: 0,
      vendor: "AWS"
    };

    let result = getDisplayItemBaseOnViewBy(account, 'account');
    expect(result).to.equal("216093335544(prod-account)");

    result = getDisplayItemBaseOnViewBy(account, 'product');
    expect(result).to.equal("prod-account");

    result = getDisplayItemBaseOnViewBy(account, 'region');
    expect(result).to.equal("prod-account");
  });

  it('Get display item with vendor base on view by', () => {
    const account = {
      item: "216093335544",
      itemAlias: "prod-account",
      order: 0,
      vendor: "AWS"
    };

    let result = getDisplayItemWithVendorBaseOnViewBy(account, 'account');
    expect(result).to.equal("AWS 216093335544(prod-account)");

    result = getDisplayItemWithVendorBaseOnViewBy(account, 'product');
    expect(result).to.equal("AWS prod-account");

    result = getDisplayItemWithVendorBaseOnViewBy(account, 'region');
    expect(result).to.equal("AWS prod-account");
  });
});
